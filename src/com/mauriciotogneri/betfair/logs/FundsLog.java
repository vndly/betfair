package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.dependency.AppObjectProvider;

public class FundsLog
{
    private static boolean firstLog = true;
    private static double lastFunds = 0;

    public static synchronized void log(double funds)
    {
        if (lastFunds != funds)
        {
            lastFunds = funds;

            if (firstLog)
            {
                firstLog = false;
                logHeader();
            }

            CsvLine csvLine = new CsvLine();
            csvLine.appendCurrentTimestamp();
            csvLine.append(funds);

            write(csvLine);
        }
    }

    private static synchronized void logHeader()
    {
        CsvLine csvLine = new CsvLine();
        csvLine.append("TIME");
        csvLine.append("FUNDS");

        write(csvLine);
    }

    private static synchronized void write(CsvLine csvLine)
    {
        try
        {
            AppObjectProvider.getFundsLog().write(csvLine);
        }
        catch (Exception e)
        {
            // ignore
        }
    }
}