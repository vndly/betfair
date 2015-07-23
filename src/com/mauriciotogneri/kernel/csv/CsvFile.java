package com.mauriciotogneri.kernel.csv;

import com.mauriciotogneri.kernel.utils.LogWriter;

import java.io.IOException;

public class CsvFile
{
    private boolean firstLine = true;
    private final LogWriter log;

    public CsvFile(String filePath) throws IOException
    {
        this.log = new LogWriter(filePath);
    }

    public void write(CsvLine csvLine) throws IOException
    {
        if (!firstLine)
        {
            log.write("\n");
        }

        log.write(csvLine.toString());

        firstLine = false;
    }
}