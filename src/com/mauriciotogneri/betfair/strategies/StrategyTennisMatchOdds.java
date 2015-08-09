package com.mauriciotogneri.betfair.strategies;

import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.api.base.Types.CancelExecutionReport;
import com.mauriciotogneri.betfair.api.base.Types.PlaceExecutionReport;
import com.mauriciotogneri.betfair.api.betting.CancelOrders;
import com.mauriciotogneri.betfair.api.betting.PlaceOrders;
import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.logs.LogWriter;
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

    private final CsvFile logPrice;
    private final LogWriter logBets;
    private final CsvFile logActionsPlayerA;
    private final CsvFile logActionsPlayerB;

    private int consecutiveValidBacks = 0;

    private final BetSimulation betSimulationPlayerA = new BetSimulation();
    private final BetSimulation betSimulationPlayerB = new BetSimulation();

    private static final int MIN_CONSECUTIVE_VALID_BACKS = 3;

    private static final double MIN_BACK_PRICE = 1.1;
    private static final double MAX_BACK_PRICE = 4.0;

    private static final double MAX_PRICE_DIFF = 1.1;
    private static final double DEFAULT_STAKE = 2;

    private static final int ONE_HOUR_BEFORE_START = -(1000 * 60 * 60); // minus one hour (-01:00:00)
    private static final int ONE_HOUR_AND_HALF_OF_PLAY = 1000 * 60 * 90; // one hour and half (01:30:00)

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

        this.logPrice = new CsvFile(logFolderPath + Log.PRICES_LOG_FILE);
        this.logBets = new LogWriter(logFolderPath + Log.BETS_LOG_FILE);
        this.logActionsPlayerA = new CsvFile(logFolderPath + "actionsA.csv");
        this.logActionsPlayerB = new CsvFile(logFolderPath + "actionsB.csv");

        initLogPrice(logPrice, selections);
    }

    private void initLogPrice(CsvFile logPrice, List<Long> selections) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.separator();

        for (Long selectionId : selections)
        {
            csvLine.append(selectionId + "-back");
            csvLine.append(selectionId + "-lay");
        }

        logPrice.write(csvLine);
    }

    @Override
    public void process(Tick tick) throws Exception
    {
        if (tick.timestamp > ONE_HOUR_BEFORE_START)
        {
            if (tick.timestamp > 0)
            {
                Selection selectionPlayerA = tick.selections.get(0);
                processSelection(Player.PLAYER_A, selectionPlayerA, betSimulationPlayerA, tick.timestamp);

                Selection selectionPlayerB = tick.selections.get(1);
                processSelection(Player.PLAYER_B, selectionPlayerB, betSimulationPlayerB, tick.timestamp);
            }

            CsvLine csvLine = new CsvLine();
            csvLine.appendTimestamp(tick.timestamp);

            for (Selection selection : tick.selections)
            {
                csvLine.append(NumberUtils.format(selection.back));
                csvLine.append(NumberUtils.format(selection.lay));
            }

            logPrice.write(csvLine);
        }
    }

    private void processSelection(Player player, Selection selection, BetSimulation betSimulation, long timestamp) throws IOException
    {
        if (betSimulation.priceBack == 0)
        {
            if (validBack(selection.back, selection.lay, timestamp))
            {
                consecutiveValidBacks++;

                if (consecutiveValidBacks >= MIN_CONSECUTIVE_VALID_BACKS)
                {
                    Budget budget = new Budget(selection.back * DEFAULT_STAKE);

                    if (Wallet.getInstance().requestBudget(budget, eventId, marketId))
                    {
                        consecutiveValidBacks = 0;

                        betSimulation.placeBackBet(selection.back, timestamp, budget);

                        logAction(player, timestamp, "BACKED AT: " + betSimulation.priceBack);
                    }
                }
            }
            else
            {
                consecutiveValidBacks = 0;
            }
        }
        else if (validLay(betSimulation.priceBack, selection.lay))
        {
            betSimulation.placeLayBet(selection.lay, timestamp);

            logAction(player, timestamp, "LAID AT:   " + selection.lay);
        }
    }

    private boolean validBack(double priceBack, double priceLay, long timestamp)
    {
        return (priceBack >= MIN_BACK_PRICE) && (priceBack <= MAX_BACK_PRICE) && ((priceLay / priceBack) <= MAX_PRICE_DIFF) && (timestamp <= ONE_HOUR_AND_HALF_OF_PLAY);
    }

    private boolean validLay(double priceBack, double priceLay)
    {
        return (priceLay < priceBack) && (priceLay > 0);
    }

    private void logAction(Player player, long timestamp, String text) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(timestamp);
        csvLine.append(text);

        switch (player)
        {
            case PLAYER_A:
                logActionsPlayerA.write(csvLine);
                break;

            case PLAYER_B:
                logActionsPlayerB.write(csvLine);
                break;
        }
    }

    private void logProfit(long timestamp, BetSimulation betSimulation) throws IOException
    {
        double profit = betSimulation.getProfit();

        CsvLine csvLine = new CsvLine();
        csvLine.appendCurrentTimestamp();
        csvLine.appendTimestamp(timestamp);
        csvLine.append(profit);
        csvLine.append(eventId);
        csvLine.append(marketId);

        csvLine.appendTimestamp(betSimulation.timestampBack);
        csvLine.append(betSimulation.priceBack);
        csvLine.append(betSimulation.stakeBack);

        csvLine.appendTimestamp(betSimulation.timestampLay);
        csvLine.append(betSimulation.priceLay);
        csvLine.append(betSimulation.stakeLay);

        ProfitLog.log(csvLine);

        Budget budget = betSimulation.getBudget();

        if (profit >= 0)
        {
            Wallet.getInstance().addProfit(budget, eventId, marketId, profit + budget.getRequested());
        }
        else
        {
            Wallet.getInstance().addProfit(budget, eventId, marketId, budget.getRest());
        }
    }

    @Override
    public void onClose(long timestamp, boolean executed) throws Exception
    {
        if (executed)
        {
            logProfit(timestamp, betSimulationPlayerA);
            logProfit(timestamp, betSimulationPlayerB);
        }
        else
        {
            CsvLine csvLine = new CsvLine();
            csvLine.append("STRATEGY NOT EXECUTED");

            logPrice.write(csvLine);
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

    private static class BetSimulation
    {
        private Budget budget = null;

        public double priceBack = 0;
        public double stakeBack = 0;
        public long timestampBack = 0;

        public double priceLay = 0;
        public double stakeLay = 0;
        public long timestampLay = 0;

        public void placeBackBet(double price, long timestamp, Budget requestedBudget)
        {
            budget = requestedBudget;

            priceBack = price;
            stakeBack = DEFAULT_STAKE;
            timestampBack = timestamp;

            // TODO: if bet is placed
            budget.use(stakeBack);
        }

        public void placeLayBet(double price, long timestamp)
        {
            priceLay = price;
            stakeLay = NumberUtils.round((stakeBack * priceBack) / priceLay, 2);
            timestampLay = timestamp;

            // TODO: if bet is placed
            double liability = (priceLay * stakeLay) * stakeLay;
            budget.use(liability);
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

        public Budget getBudget()
        {
            return budget;
        }
    }
}