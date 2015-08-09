package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.dependency.AppObjectProvider;
import com.mauriciotogneri.betfair.utils.TimeUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorLog
{
    public static synchronized void log(String message)
    {
        try
        {
            AppObjectProvider.getErrorLog().writeLn(TimeUtils.getTimestamp() + "\t" + message);
        }
        catch (Exception e)
        {
            // ignore
        }
    }

    public static synchronized void log(Exception e)
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);

        log(stringWriter.toString());
    }
}