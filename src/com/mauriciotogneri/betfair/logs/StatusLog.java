package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.csv.CsvLine;

import java.io.IOException;

public class StatusLog extends CsvFile
{
    public StatusLog(String filePath) throws IOException
    {
        super(filePath);

        logHeader();
    }

    public synchronized void log(long timestamp, String status) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(timestamp);
        csvLine.append(status);

        write(csvLine);
    }

    private void logHeader() throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.append("TIME");
        csvLine.append("STATUS");

        write(csvLine);
    }
}