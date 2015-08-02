package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.dependency.AppObjectProvider;
import com.mauriciotogneri.betfair.utils.TimeUtils;

public class ActivityLog
{
    public static synchronized void log(String message)
    {
        try
        {
            AppObjectProvider.getActivityLog().write(TimeUtils.getTimestamp() + "\t" + message + "\n");
        }
        catch (Exception e)
        {
            // ignore
        }
    }
}