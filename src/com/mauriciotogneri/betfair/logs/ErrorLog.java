package com.mauriciotogneri.betfair.logs;

import com.google.gson.JsonSyntaxException;
import com.mauriciotogneri.betfair.dependency.AppObjectProvider;
import com.mauriciotogneri.betfair.utils.StringUtils;
import com.mauriciotogneri.betfair.utils.TimeUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;

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
        if (isValidException(e))
        {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);

            log(stringWriter.toString());
        }
    }

    private static boolean isValidException(Exception e)
    {
        if (e instanceof SocketException)
        {
            if (StringUtils.contains(e.getMessage(), "Connection reset") && StringUtils.contains(e.getMessage(), "MarketMonitor"))
            {
                return false;
            }
        }
        else if (e instanceof JsonSyntaxException)
        {
            if (StringUtils.contains(e.getMessage(), "Expected BEGIN_ARRAY but was BEGIN_OBJECT") && StringUtils.contains(e.getMessage(), "MarketMonitor"))
            {
                return false;
            }
        }

        return true;
    }
}