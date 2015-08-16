package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.dependency.AppObjectProvider;
import com.mauriciotogneri.betfair.models.Wallet.Type;
import com.mauriciotogneri.betfair.utils.NumberUtils;

import java.io.IOException;

public class WalletLog
{
    private static boolean firstLog = true;

    public static synchronized void log(Type type, int budgetId, String eventId, String marketId, String player, double value, double balance) throws IOException
    {
        if (firstLog)
        {
            firstLog = false;
            logHeader();
        }

        CsvLine csvLine = new CsvLine();
        csvLine.appendCurrentTimestamp();
        csvLine.append(type.toString());
        csvLine.append(budgetId);
        csvLine.append(eventId);
        csvLine.append(marketId);
        csvLine.append(player);
        csvLine.append(NumberUtils.round(value, 2));
        csvLine.append(NumberUtils.round(balance, 2));

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
        csvLine.append("PLAYER");
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
            ErrorLog.log(e);
        }
    }
}