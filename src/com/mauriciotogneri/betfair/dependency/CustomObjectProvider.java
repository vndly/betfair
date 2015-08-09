package com.mauriciotogneri.betfair.dependency;

import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.logs.LogWriter;

import java.io.IOException;

public class CustomObjectProvider implements ObjectProvider
{
    private LogWriter errorLog;
    private LogWriter threadLog;
    private LogWriter activityLog;
    private CsvFile profitLog;
    private CsvFile walletLog;
    private CsvFile fundsLog;

    @Override
    public LogWriter getErrorLog() throws IOException
    {
        return (errorLog == null) ? (errorLog = new LogWriter(Log.ERROR_LOG_PATH)) : errorLog;
    }

    @Override
    public LogWriter getThreadLog() throws IOException
    {
        return (threadLog == null) ? (threadLog = new LogWriter(Log.THREAD_LOG_PATH)) : threadLog;
    }

    @Override
    public CsvFile getProfitLog() throws IOException
    {
        return (profitLog == null) ? (profitLog = new CsvFile(Log.PROFIT_LOG_PATH)) : profitLog;
    }

    @Override
    public CsvFile getWalletLog() throws IOException
    {
        return (walletLog == null) ? (walletLog = new CsvFile(Log.WALLET_LOG_PATH)) : walletLog;
    }

    @Override
    public LogWriter getActivityLog() throws IOException
    {
        return (activityLog == null) ? (activityLog = new LogWriter(Log.ACTIVITY_LOG_PATH)) : activityLog;
    }

    @Override
    public CsvFile getFundsLog() throws IOException
    {
        return (fundsLog == null) ? (fundsLog = new CsvFile(Log.FUNDS_LOG_PATH)) : fundsLog;
    }
}