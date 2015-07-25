package com.mauriciotogneri.kernel.dependency;

import com.mauriciotogneri.kernel.logs.LogWriter;

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
}