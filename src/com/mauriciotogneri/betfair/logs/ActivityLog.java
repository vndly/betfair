package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.dependency.AppObjectProvider;
import com.mauriciotogneri.betfair.utils.TimeUtils;

public class ActivityLog
{
    public static synchronized void log(String message)
    {
        try
        {
            AppObjectProvider.getActivityLog().writeLn(TimeUtils.getTimestamp() + "\t" + message);
        }
        catch (Exception e)
        {
            ErrorLog.log(e);
        }
    }
}