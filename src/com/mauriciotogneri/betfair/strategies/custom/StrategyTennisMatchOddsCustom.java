package com.mauriciotogneri.betfair.strategies.custom;

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
import com.mauriciotogneri.betfair.models.BetSimulation;
import com.mauriciotogneri.betfair.models.Budget;
import com.mauriciotogneri.betfair.models.Tick;
import com.mauriciotogneri.betfair.models.Wallet;
import com.mauriciotogneri.betfair.strategies.Strategy;
import com.mauriciotogneri.betfair.strategies.kernel.StrategyTennisMatchOdds;
import com.mauriciotogneri.betfair.utils.JsonUtils;

import java.io.IOException;
import java.util.List;

public class StrategyTennisMatchOddsCustom extends StrategyTennisMatchOdds
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

    private Budget budgetPlayerA = null;
    private Budget budgetPlayerB = null;

    public StrategyTennisMatchOddsCustom(Session session, String eventId, String marketId, List<Long> selections, String logFolderPath) throws IOException
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

    private void logProfit(long timestamp, Player player, BetSimulation betSimulation) throws IOException
    {
        logActivity.writeLn("LOG PROFIT: " + player + " - " + JsonUtils.toJson(betSimulation));

        Budget budget = getBudget(player);
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

    private Budget getBudget(Player player)
    {
        switch (player)
        {
            case PLAYER_A:
                return budgetPlayerA;

            case PLAYER_B:
                return budgetPlayerB;
        }

        return null;
    }

    private Budget setBudget(Player player, double value)
    {
        switch (player)
        {
            case PLAYER_A:
                budgetPlayerA = new Budget(value);
                return budgetPlayerA;

            case PLAYER_B:
                budgetPlayerB = new Budget(value);
                return budgetPlayerB;
        }

        return null;
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

    @Override
    protected void onTick(Tick tick) throws IOException
    {
        logPrice.log(tick.timestamp, tick.selections);
    }

    @Override
    protected boolean onRequestBudget(Strategy.Player player, double value) throws IOException
    {
        Budget budget = setBudget(player, value);

        return Wallet.getInstance().withdraw(budget, eventId, marketId, player.toString());
    }

    @Override
    protected void onUseBudget(Strategy.Player player, double value) throws IOException
    {
        Budget budget = getBudget(player);

        if (budget != null)
        {
            budget.use(value);
        }
    }

    @Override
    protected void onReturnBudget(Strategy.Player player) throws IOException
    {
        Budget budget = getBudget(player);

        if (budget != null)
        {
            Wallet.getInstance().deposit(budget, eventId, marketId, player.toString(), budget.getRequested());
        }
    }

    @Override
    protected boolean onPlaceBackBet(Strategy.Player player, double price, double stake, long timestamp) throws IOException
    {
        // TODO

        logAction(player, timestamp, "BACKED AT: " + price);

        return true;
    }

    @Override
    protected boolean onPlaceLayBet(Strategy.Player player, double price, long timestamp) throws IOException
    {
        // TODO

        logAction(player, timestamp, "LAID AT:   " + price);

        return true;
    }

    @Override
    protected void onLayPriceLow(Strategy.Player player, long timestamp, double value) throws IOException
    {
        logAction(player, timestamp, "LOW PRICE: " + value);
    }

    @Override
    protected void onClose(Strategy.Player player, boolean executed, long timestamp, BetSimulation betSimulation) throws IOException
    {
        if (executed)
        {
            logProfit(timestamp, player, betSimulation);
        }
        else
        {
            logActivity.writeLn("STRATEGY NOT EXECUTED");
        }
    }


    @Override
    public void onClose(long timestamp, boolean executed) throws Exception
    {

    }
}