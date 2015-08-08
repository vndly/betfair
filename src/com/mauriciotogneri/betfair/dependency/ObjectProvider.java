package com.mauriciotogneri.betfair.dependency;

import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.logs.LogWriter;

import java.io.IOException;

public interface ObjectProvider
{
    LogWriter getErrorLog() throws IOException;

    LogWriter getActivityLog() throws IOException;

    CsvFile getProfitLog() throws IOException;

    CsvFile getWalletLog() throws IOException;

    CsvFile getFundsLog() throws IOException;
}