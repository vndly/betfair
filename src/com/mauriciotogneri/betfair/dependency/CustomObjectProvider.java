package com.mauriciotogneri.betfair.dependency;

import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.logs.LogWriter;

import java.io.IOException;

public class CustomObjectProvider implements ObjectProvider
{
    private final String errorLogPath;
    private final String activityLogPath;
    private final String profitLogPath;
    private final String walletLogPath;
    private final String fundsLogPath;

    // singleton instances
    private LogWriter errorLog;
    private LogWriter activityLog;
    private CsvFile profitLog;
    private CsvFile walletLog;
    private CsvFile fundsLog;

    public CustomObjectProvider(String errorLogPath, String profitLogPath, String activityLogPath, String fundsLogPath, String walletLogPath)
    {
        this.errorLogPath = errorLogPath;
        this.profitLogPath = profitLogPath;
        this.activityLogPath = activityLogPath;
        this.fundsLogPath = fundsLogPath;
        this.walletLogPath = walletLogPath;
    }

    @Override
    public LogWriter getErrorLog() throws IOException
    {
        return (errorLog == null) ? (errorLog = new LogWriter(errorLogPath)) : errorLog;
    }

    @Override
    public CsvFile getProfitLog() throws IOException
    {
        return (profitLog == null) ? (profitLog = new CsvFile(profitLogPath)) : profitLog;
    }

    @Override
    public CsvFile getWalletLog() throws IOException
    {
        return (walletLog == null) ? (walletLog = new CsvFile(walletLogPath)) : walletLog;
    }

    @Override
    public LogWriter getActivityLog() throws IOException
    {
        return (activityLog == null) ? (activityLog = new LogWriter(activityLogPath)) : activityLog;
    }

    @Override
    public CsvFile getFundsLog() throws IOException
    {
        return (fundsLog == null) ? (fundsLog = new CsvFile(fundsLogPath)) : fundsLog;
    }
}