package com.mauriciotogneri.betfair.logs;

import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.models.Selection;
import com.mauriciotogneri.betfair.utils.NumberUtils;

import java.io.IOException;
import java.util.List;

public class PriceLog extends CsvFile
{
    public PriceLog(String filePath, List<Long> selections) throws IOException
    {
        super(filePath);

        logHeader(selections);
    }

    public synchronized void log(long timestamp, List<Selection> selections) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(timestamp);

        for (Selection selection : selections)
        {
            csvLine.append(NumberUtils.format(selection.back));
            csvLine.append(NumberUtils.format(selection.lay));
        }

        write(csvLine);
    }

    private void logHeader(List<Long> selections) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.append("TIME");

        for (Long selectionId : selections)
        {
            csvLine.append(selectionId + "-back");
            csvLine.append(selectionId + "-lay");
        }

        write(csvLine);
    }
}