package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.dependency.AppObjectProvider;
import com.mauriciotogneri.betfair.strategies.StrategyTennisMatchOdds.BetSimulation;

public class ProfitLog
{
    private static boolean firstLog = true;

    public static synchronized void log(long timestamp, double profit, String budgetId, BetSimulation betSimulation, String eventId, String marketId, String player)
    {
        if (firstLog)
        {
            firstLog = false;
            logHeader();
        }

        CsvLine csvLine = new CsvLine();
        csvLine.appendCurrentTimestamp();
        csvLine.appendTimestamp(timestamp);
        csvLine.append(profit);
        csvLine.append(budgetId);
        csvLine.append(betSimulation.getBudgetRequestsFailed());
        csvLine.append(eventId);
        csvLine.append(marketId);
        csvLine.append(player);

        csvLine.appendTimestamp(betSimulation.getTimestampBack());
        csvLine.append(betSimulation.getPriceBack());
        csvLine.append(betSimulation.getStakeBack());
        csvLine.append(betSimulation.getBackBetFailed());

        csvLine.appendTimestamp(betSimulation.getTimestampLay());
        csvLine.append(betSimulation.getPriceLay());
        csvLine.append(betSimulation.getStakeLay());
        csvLine.append(betSimulation.getLayBetFailed());

        csvLine.append(betSimulation.getLowPriceAverage());
        csvLine.append(betSimulation.getLowPriceCount());

        write(csvLine);
    }

    private static synchronized void logHeader()
    {
        CsvLine csvLine = new CsvLine();
        csvLine.append("TIME");
        csvLine.append("MATCH TIME");
        csvLine.append("PROFIT");
        csvLine.append("BUDGET ID");
        csvLine.append("BUDGET FAILED");
        csvLine.append("EVENT ID");
        csvLine.append("MARKET ID");
        csvLine.append("PLAYER");
        csvLine.append("BACK TIME");
        csvLine.append("BACK PRICE");
        csvLine.append("BACK STEAK");
        csvLine.append("BACK BET FAILED");
        csvLine.append("LAY TIME");
        csvLine.append("LAY PRICE");
        csvLine.append("LAY STEAK");
        csvLine.append("LAY BET FAILED");
        csvLine.append("LAY PRICE AVG.");
        csvLine.append("LAY PRICE COUNT");

        write(csvLine);
    }

    private static synchronized void write(CsvLine csvLine)
    {
        try
        {
            AppObjectProvider.getProfitLog().write(csvLine);
        }
        catch (Exception e)
        {
            ErrorLog.log(e);
        }
    }
}