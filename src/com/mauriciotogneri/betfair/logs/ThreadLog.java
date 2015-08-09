package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.dependency.AppObjectProvider;
import com.mauriciotogneri.betfair.utils.TimeUtils;

import java.util.List;

public class ThreadLog
{
    public static synchronized void log(List<Thread> threads)
    {
        try
        {
            AppObjectProvider.getThreaddLog().writeLn(TimeUtils.getTimestamp() + "\t" + getMessage(threads));
        }
        catch (Exception e)
        {
            // ignore
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
        }

        builder.append("\n");

        return builder.toString();
    }
}