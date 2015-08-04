package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.dependency.AppObjectProvider;

public class ProfitLog
{
    private static boolean firstLog = true;

    public static synchronized void log(String message)
    {
        if (firstLog)
        {
            firstLog = false;
            logHeader();
        }

        write(message + "\n");
    }

    private static synchronized void logHeader()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("TIME,");
        builder.append("MATCH TIME,");
        builder.append("PROFIT,");
        builder.append("EVENT ID,");
        builder.append("MARKET ID,");
        builder.append("BACK TIME,");
        builder.append("BACK PRICE,");
        builder.append("BACK STEAK,");
        builder.append("LAY TIME,");
        builder.append("LAY PRICE,");
        builder.append("LAY STEAK\n");

        write(builder.toString());
    }

    private static synchronized void write(String message)
    {
        try
        {
            AppObjectProvider.getProfitLog().write(message);
        }
        catch (Exception e)
        {
            // ignore
        }
    }
}