package com.mauriciotogneri.betfair.dependency;

import com.mauriciotogneri.betfair.logs.LogWriter;

import java.io.IOException;

public class CustomObjectProvider implements ObjectProvider
{
    private final String errorLogPath;
    private final String profitLogPath;
    private final String activityLogPath;

    // singleton instances
    private LogWriter errorLog;
    private LogWriter profitLog;
    private LogWriter activityLog;

    public CustomObjectProvider(String errorLogPath, String profitLogPath, String activityLogPath)
    {
        this.errorLogPath = errorLogPath;
        this.profitLogPath = profitLogPath;
        this.activityLogPath = activityLogPath;
    }

    @Override
    public LogWriter getErrorLog() throws IOException
    {
        return (errorLog == null) ? (errorLog = new LogWriter(errorLogPath)) : errorLog;
    }

    @Override
    public LogWriter getProfitLog() throws IOException
    {
        return (profitLog == null) ? (profitLog = new LogWriter(profitLogPath)) : profitLog;
    }

    @Override
    public LogWriter getActivityLog() throws IOException
    {
        return (activityLog == null) ? (activityLog = new LogWriter(activityLogPath)) : activityLog;
    }
}