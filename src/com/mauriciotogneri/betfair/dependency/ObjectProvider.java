package com.mauriciotogneri.betfair.dependency;

import com.mauriciotogneri.betfair.logs.LogWriter;

import java.io.IOException;

public interface ObjectProvider
{
    LogWriter getErrorLog() throws IOException;

    LogWriter getProfitLog() throws IOException;

    LogWriter getActivityLog() throws IOException;
}