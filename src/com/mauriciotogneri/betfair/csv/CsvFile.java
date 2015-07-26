package com.mauriciotogneri.betfair.csv;

import com.mauriciotogneri.betfair.logs.LogWriter;

import java.io.IOException;

public class CsvFile
{
    private boolean firstLine = true;
    private final LogWriter log;

    public CsvFile(String filePath) throws IOException
    {
        this.log = new LogWriter(filePath);
    }

    public synchronized void write(CsvLine csvLine) throws IOException
    {
        if (!firstLine)
        {
            log.write("\n");
        }

        log.write(csvLine.toString());

        firstLine = false;
    }
}