package com.mauriciotogneri.betfair.strategies;

import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.api.base.Types.CancelExecutionReport;
import com.mauriciotogneri.betfair.api.base.Types.PlaceExecutionReport;
import com.mauriciotogneri.betfair.api.betting.CancelOrders;
import com.mauriciotogneri.betfair.api.betting.PlaceOrders;
import com.mauriciotogneri.betfair.logs.ActionsLog;
import com.mauriciotogneri.betfair.logs.LogWriter;
import com.mauriciotogneri.betfair.logs.PriceLog;
import com.mauriciotogneri.betfair.logs.ProfitLog;
import com.mauriciotogneri.betfair.models.Bet;
import com.mauriciotogneri.betfair.models.BetInstruction;
import com.mauriciotogneri.betfair.models.Budget;
import com.mauriciotogneri.betfair.models.Selection;
import com.mauriciotogneri.betfair.models.Tick;
import com.mauriciotogneri.betfair.models.Wallet;
import com.mauriciotogneri.betfair.utils.JsonUtils;
import com.mauriciotogneri.betfair.utils.NumberUtils;

import java.io.IOException;
import java.util.List;

public class StrategyTennisMatchOdds extends Strategy
{
    private final String eventId;
    private final String marketId;
    private final Session session;
    private final HttpClient httpClient;

    private final PriceLog logPrice;
    private final LogWriter logActivity;
    private final LogWriter logBets;
    private final ActionsLog logActionsPlayerA;
    private final ActionsLog logActionsPlayerB;

    private final BetSimulation betSimulationPlayerA = new BetSimulation();
    private final BetSimulation betSimulationPlayerB = new BetSimulation();

    private static final int MIN_CONSECUTIVE_VALID_BACKS = 3;
    private static final int MAX_BUDGET_REQUEST_FAILS = 10;

    private static final double MIN_BACK_PRICE = 1.1;
    private static final double MAX_BACK_PRICE = 4.0;

    private static final double MAX_PRICE_DIFF = 1.1;
    private static final double IDEAL_PRICE_FACTOR = 0.8;
    private static final double DEFAULT_STAKE = 2;

    private enum Player
    {
        PLAYER_A, //
        PLAYER_B
    }

    public StrategyTennisMatchOdds(Session session, String eventId, String marketId, List<Long> selections, String logFolderPath) throws IOException
    {
        this.session = session;
        this.eventId = eventId;
        this.marketId = marketId;
        this.httpClient = HttpClient.getDefault();

        this.logPrice = new PriceLog(logFolderPath + Log.PRICES_LOG_FILE, selections);
        this.logActivity = new LogWriter(logFolderPath + Log.ACTIVITY_LOG_FILE);
        this.logBets = new LogWriter(logFolderPath + Log.BETS_LOG_FILE);
        this.logActionsPlayerA = new ActionsLog(logFolderPath + "actionsA.csv");
        this.logActionsPlayerB = new ActionsLog(logFolderPath + "actionsB.csv");
    }

    @Override
    public boolean process(Tick tick) throws Exception
    {
        if (isMoreThan(tick.timestamp, -1))
        {
            if (tick.timestamp > 0)
            {
                Selection selectionPlayerA = tick.selections.get(0);
                processSelection(Player.PLAYER_A, selectionPlayerA, betSimulationPlayerA, tick.timestamp);

                Selection selectionPlayerB = tick.selections.get(1);
                processSelection(Player.PLAYER_B, selectionPlayerB, betSimulationPlayerB, tick.timestamp);
            }

            logPrice.log(tick.timestamp, tick.selections);
        }

        return (betSimulationPlayerA.isBacked() || betSimulationPlayerB.isBacked() || isLessThan(tick.timestamp, 5));
    }

    private void processSelection(Player player, Selection selection, BetSimulation betSimulation, long timestamp) throws IOException
    {
        if (!betSimulation.isBacked())
        {
            if (validBack(selection.back, selection.lay, timestamp))
            {
                if (betSimulation.addConsecutiveValidBack() && betSimulation.failedBudgetRequestValid())
                {
                    Budget budget = new Budget(selection.back * DEFAULT_STAKE);

                    if (Wallet.getInstance().withdraw(budget, eventId, marketId, player.toString()))
                    {
                        boolean betPlaced = betSimulation.placeBackBet(selection.back, timestamp, budget);

                        if (betPlaced)
                        {
                            logAction(player, timestamp, "BACKED AT: " + betSimulation.priceBack);
                        }
                        else
                        {
                            Wallet.getInstance().deposit(budget, eventId, marketId, player.toString(), budget.getRequested());
                        }
                    }
                    else
                    {
                        betSimulation.failBudgetRequest();
                    }
                }
            }
            else
            {
                betSimulation.resetConsecutiveValidBack();
            }
        }
        else if (validLay(betSimulation.priceBack, selection.lay, timestamp))
        {
            if (!betSimulation.isLaid())
            {
                boolean betPlaced = betSimulation.placeLayBet(selection.lay, timestamp);

                if (betPlaced)
                {
                    logAction(player, timestamp, "LAID AT:   " + selection.lay);
                }
            }
            else
            {
                logAction(player, timestamp, "LOW PRICE: " + selection.lay);
            }

            betSimulation.saveLowPrice(selection.lay);
        }
    }

    private boolean validBack(double priceBack, double priceLay, long timestamp)
    {
        boolean minPriceValueValid = priceBack >= MIN_BACK_PRICE;
        boolean maxPriceValueValid = priceBack <= MAX_BACK_PRICE;
        boolean maxPriceDiffValid = (priceLay / priceBack) <= MAX_PRICE_DIFF;
        boolean minTimeLimitValid = isMoreThan(timestamp, 0.25);
        boolean maxTimeLimitValid = isLessThan(timestamp, 1);

        return (minPriceValueValid && maxPriceValueValid && maxPriceDiffValid && minTimeLimitValid && maxTimeLimitValid);
    }

    private boolean validLay(double priceBack, double priceLay, long timestamp)
    {
        boolean priceLowerThanBack = priceLay < priceBack;
        boolean priceBiggerThanZero = priceLay > 0;
        boolean isAfterHalfHour = isMoreThan(timestamp, 0.5);
        boolean idealPriceValid = priceLay <= (priceBack * IDEAL_PRICE_FACTOR);

        return (priceLowerThanBack && priceBiggerThanZero && (isAfterHalfHour || idealPriceValid));
    }

    private void logAction(Player player, long timestamp, String action) throws IOException
    {
        switch (player)
        {
            case PLAYER_A:
                logActionsPlayerA.log(timestamp, action);
                break;

            case PLAYER_B:
                logActionsPlayerB.log(timestamp, action);
                break;
        }
    }

    private void logProfit(long timestamp, Player player, BetSimulation betSimulation) throws IOException
    {
        logActivity.writeLn("LOG PROFIT: " + player + " - " + JsonUtils.toJson(betSimulation));

        Budget budget = betSimulation.budget;
        double profit = betSimulation.getProfit();
        String budgetId = (budget != null) ? String.valueOf(budget.getId()) : "";

        ProfitLog.log(timestamp, profit, budgetId, betSimulation, eventId, marketId, player.toString());

        if (budget != null)
        {
            if (profit >= 0)
            {
                logActivity.writeLn("LOG DEPOSIT WIN: " + player + " => " + (profit + budget.getRequested()));

                Wallet.getInstance().deposit(budget, eventId, marketId, player.toString(), profit + budget.getRequested());
            }
            else
            {
                logActivity.writeLn("LOG DEPOSIT LOSE: " + player + " => " + budget.getRest());

                Wallet.getInstance().deposit(budget, eventId, marketId, player.toString(), budget.getRest());
            }
        }
        else
        {
            logActivity.writeLn("LOG NO BUDGET ACQUIRED: " + player);
        }
    }

    private boolean isMoreThan(long timestamp, double hours)
    {
        return timestamp >= (1000 * 60 * (60 * hours));
    }

    private boolean isLessThan(long timestamp, double hours)
    {
        return timestamp <= (1000 * 60 * (60 * hours));
    }

    @Override
    public void onClose(long timestamp, boolean executed) throws Exception
    {
        if (executed)
        {
            logActivity.writeLn("LOGGING PROFITS");

            logProfit(timestamp, Player.PLAYER_A, betSimulationPlayerA);
            logProfit(timestamp, Player.PLAYER_B, betSimulationPlayerB);
        }
        else
        {
            logActivity.writeLn("STRATEGY NOT EXECUTED");
        }
    }

    private Bet placeBet(BetInstruction betInstruction) throws IOException
    {
        //        String marketId = "1.119607159";
        //        long selectionId = 1221386;
        //        Side side = Side.LAY;
        //        double price = 1.73;
        //        double stake = 2.01;

        //        String marketId = "1.119607159x";
        //        long selectionId = 1221386;
        //        Side side = Side.BACK;
        //        double price = 1.74;
        //        double stake = 2;

        PlaceOrders placeOrders = PlaceOrders.getRequest(httpClient, session, betInstruction);
        PlaceExecutionReport placeExecutionReport = placeOrders.execute();

        logBets.writeLn(JsonUtils.toJson(placeExecutionReport));

        if (placeExecutionReport.isValid())
        {
            logBets.writeLn("PLACED");

            Bet bet = placeExecutionReport.getBet(betInstruction);
            logBets.writeLn(JsonUtils.toJson(bet));

            if (!bet.isMatched)
            {
                CancelOrders cancelOrders = CancelOrders.getRequest(httpClient, session, bet);
                CancelExecutionReport cancelExecutionReport = cancelOrders.execute();

                logBets.writeLn(JsonUtils.toJson(cancelExecutionReport));

                if (cancelExecutionReport.isValid())
                {
                    logBets.writeLn("CANCELLED");
                }
                else
                {
                    logBets.writeLn("NOT CANCELLED");

                    return bet;
                }
            }
            else
            {
                return bet;
            }
        }
        else
        {
            logBets.writeLn("NOT PLACED");
        }

        return null;
    }

    public static class BetSimulation
    {
        private Budget budget = null;

        private double priceBack = 0;
        private double stakeBack = 0;
        private long timestampBack = 0;
        private int backBetFailed = 0;

        private double priceLay = 0;
        private double stakeLay = 0;
        private long timestampLay = 0;
        private int layBetFailed = 0;

        private double lowPriceSum = 0;

        private int lowPriceCount = 0;

        private int consecutiveValidBacks = 0;
        private int budgetRequestsFailed = 0;

        public boolean placeBackBet(double price, long timestamp, Budget requestedBudget)
        {
            boolean placed = true; // TODO

            if (placed)
            {
                budget = requestedBudget;

                priceBack = price;
                stakeBack = DEFAULT_STAKE;
                timestampBack = timestamp;

                budget.use(stakeBack);
            }
            else
            {
                backBetFailed++;
            }

            return placed;
        }

        public boolean placeLayBet(double price, long timestamp)
        {
            boolean placed = true; // TODO

            if (placed)
            {
                priceLay = price;
                stakeLay = NumberUtils.round((stakeBack * priceBack) / priceLay, 2);
                timestampLay = timestamp;

                double liability = (priceLay * stakeLay) * stakeLay;
                budget.use(liability);
            }
            else
            {
                layBetFailed++;
            }

            return placed;
        }

        public double getPriceBack()
        {
            return priceBack;
        }

        public double getStakeBack()
        {
            return stakeBack;
        }

        public long getTimestampBack()
        {
            return timestampBack;
        }

        public int getBackBetFailed()
        {
            return backBetFailed;
        }

        public double getPriceLay()
        {
            return priceLay;
        }

        public double getStakeLay()
        {
            return stakeLay;
        }

        public long getTimestampLay()
        {
            return timestampLay;
        }

        public int getLayBetFailed()
        {
            return layBetFailed;
        }

        public int getBudgetRequestsFailed()
        {
            return budgetRequestsFailed;
        }

        public boolean addConsecutiveValidBack()
        {
            consecutiveValidBacks++;

            return consecutiveValidBacks >= MIN_CONSECUTIVE_VALID_BACKS;
        }

        public void resetConsecutiveValidBack()
        {
            consecutiveValidBacks = 0;
        }

        public void failBudgetRequest()
        {
            budgetRequestsFailed++;
        }

        public boolean failedBudgetRequestValid()
        {
            return budgetRequestsFailed < MAX_BUDGET_REQUEST_FAILS;
        }

        public void saveLowPrice(double value)
        {
            lowPriceSum += value;
            lowPriceCount++;
        }

        public double getLowPriceAverage()
        {
            return (lowPriceCount > 0) ? NumberUtils.round(lowPriceSum / lowPriceCount, 2) : 0;
        }

        public int getLowPriceCount()
        {
            return lowPriceCount;
        }

        public boolean isBacked()
        {
            return (priceBack != 0);
        }

        public boolean isLaid()
        {
            return (priceLay != 0);
        }

        public double getProfit()
        {
            if (priceBack != 0)
            {
                if (priceLay != 0)
                {
                    double ifWin = (stakeBack * priceBack) - stakeBack;
                    double ifLose = (stakeLay * priceLay) - stakeLay;

                    return NumberUtils.round(ifWin - ifLose, 2);
                }
                else
                {
                    return -stakeBack; // we assume that we lose the bet
                }
            }

            return 0;
        }
    }
}