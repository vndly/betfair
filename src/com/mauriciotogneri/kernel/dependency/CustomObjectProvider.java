package com.mauriciotogneri.kernel.dependency;

import com.mauriciotogneri.kernel.logs.LogWriter;

import java.io.IOException;

public class CustomObjectProvider implements ObjectProvider
{
    private final String errorLogPath;

    // singleton instances
    private LogWriter errorLog;

    public CustomObjectProvider(String errorLogPath)
    {
        this.errorLogPath = errorLogPath;
    }

    @Override
    public LogWriter getErrorLog() throws IOException
    {
        return (errorLog == null) ? (errorLog = new LogWriter(errorLogPath)) : errorLog;
    }
}