package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.csv.CsvLine;

import java.io.IOException;

public class ActionsLog extends CsvFile
{
    public ActionsLog(String filePath) throws IOException
    {
        super(filePath);

        logHeader();
    }

    public synchronized void log(long timestamp, String action) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(timestamp);
        csvLine.append(action);

        write(csvLine);
    }

    private void logHeader() throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.append("TIME");
        csvLine.append("ACTION");

        write(csvLine);
    }
}