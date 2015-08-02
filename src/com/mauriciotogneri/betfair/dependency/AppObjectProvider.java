package com.mauriciotogneri.betfair.dependency;

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

    public static LogWriter getProfitLog() throws IOException
    {
        return instance.getProfitLog();
    }

    public static LogWriter getActivityLog() throws IOException
    {
        return instance.getActivityLog();
    }
}