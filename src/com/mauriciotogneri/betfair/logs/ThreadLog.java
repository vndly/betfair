package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.dependency.AppObjectProvider;
import com.mauriciotogneri.betfair.monitors.AbstractMonitor;
import com.mauriciotogneri.betfair.utils.TimeUtils;

import java.util.List;

public class ThreadLog
{
    public static synchronized void log(List<Thread> threads)
    {
        try
        {
            AppObjectProvider.getThreadLog().writeLn(TimeUtils.getTimestamp() + "\t" + getMessage(threads));
        }
        catch (Exception e)
        {
            ErrorLog.log(e);
        }
    }

    private static String getMessage(List<Thread> threads)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("TOTAL THREADS: ");
        builder.append(threads.size());

        for (Thread thread : threads)
        {
            builder.append("\n");
            builder.append(thread.getName());
            builder.append(" - ");
            builder.append(thread.getId());

            if (thread instanceof AbstractMonitor)
            {
                builder.append(" - ");
                builder.append(((AbstractMonitor) thread).elapsedTime());
            }
        }

        builder.append("\n");

        return builder.toString();
    }
}