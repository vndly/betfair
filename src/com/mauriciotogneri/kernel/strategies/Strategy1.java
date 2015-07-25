package com.mauriciotogneri.kernel.strategies;

import com.mauriciotogneri.kernel.csv.CsvFile;
import com.mauriciotogneri.kernel.csv.CsvLine;
import com.mauriciotogneri.kernel.models.Selection;
import com.mauriciotogneri.kernel.models.Tick;

import java.io.IOException;
import java.util.List;

public class Strategy1 extends Strategy
{
    private CsvFile logPrice;

    public Strategy1(List<Long> selections, String folderPath, String marketId, String marketType) throws IOException
    {
        logPrice = new CsvFile(folderPath + "/" + marketId + "-" + marketType + ".csv");

        CsvLine csvLine = new CsvLine();
        csvLine.separator();

        for (long selectionId : selections)
        {
            csvLine.append(selectionId + "-back");
            csvLine.append(selectionId + "-lay");
        }

        logPrice.write(csvLine);
    }

    @Override
    public void process(Tick tick) throws Exception
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(tick.timestamp);

        for (Selection selection : tick.selections) {
            csvLine.append(selection.back);
            csvLine.append(selection.lay);
        }

        logPrice.write(csvLine);
    }
}