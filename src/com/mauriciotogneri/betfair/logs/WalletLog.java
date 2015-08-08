package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.dependency.AppObjectProvider;

public class WalletLog
{
    private static boolean firstLog = true;

    public static synchronized void log(CsvLine csvLine)
    {
        if (firstLog)
        {
            firstLog = false;
            logHeader();
        }

        write(csvLine);
    }

    private static synchronized void logHeader()
    {
        CsvLine csvLine = new CsvLine();
        csvLine.append("TIME");
        csvLine.append("TYPE");
        csvLine.append("BUDGET ID");
        csvLine.append("EVENT ID");
        csvLine.append("MARKET ID");
        csvLine.append("VALUE");
        csvLine.append("BALANCE");

        write(csvLine);
    }

    private static synchronized void write(CsvLine csvLine)
    {
        try
        {
            AppObjectProvider.getWalletLog().write(csvLine);
        }
        catch (Exception e)
        {
            // ignore
        }
    }
}