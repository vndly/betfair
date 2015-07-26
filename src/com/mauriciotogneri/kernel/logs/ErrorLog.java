package com.mauriciotogneri.kernel.logs;

import com.mauriciotogneri.kernel.dependency.AppObjectProvider;
import com.mauriciotogneri.kernel.utils.TimeFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorLog
{
    public static synchronized void log(String message)
    {
        try
        {
            AppObjectProvider.getErrorLog().write(TimeFormatter.getTimestamp() + "\t" + message + "\n");
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