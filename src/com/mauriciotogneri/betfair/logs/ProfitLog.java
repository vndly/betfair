package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.dependency.AppObjectProvider;

public class ProfitLog
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
        csvLine.append("MATCH TIME");
        csvLine.append("PROFIT");
        csvLine.append("EVENT ID");
        csvLine.append("MARKET ID");
        csvLine.append("PLAYER");
        csvLine.append("BACK TIME");
        csvLine.append("BACK PRICE");
        csvLine.append("BACK STEAK");
        csvLine.append("LAY TIME");
        csvLine.append("LAY PRICE");
        csvLine.append("LAY STEAK");
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