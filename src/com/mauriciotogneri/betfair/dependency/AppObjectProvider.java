package com.mauriciotogneri.betfair.dependency;

import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.logs.LogWriter;

import java.io.IOException;

public class AppObjectProvider
{
    private static ObjectProvider instance;

    public static void init(ObjectProvider objectProvider)
    {
        instance = objectProvider;
    }

    public static LogWriter getErrorLog() throws IOException
    {
        return instance.getErrorLog();
    }

    public static LogWriter getThreaddLog() throws IOException
    {
        return instance.getThreadLog();
    }

    public static CsvFile getProfitLog() throws IOException
    {
        return instance.getProfitLog();
    }

    public static CsvFile getWalletLog() throws IOException
    {
        return instance.getWalletLog();
    }

    public static LogWriter getActivityLog() throws IOException
    {
        return instance.getActivityLog();
    }

    public static CsvFile getFundsLog() throws IOException
    {
        return instance.getFundsLog();
    }
}