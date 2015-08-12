package com.mauriciotogneri.betfair.strategies;

import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.models.Selection;
import com.mauriciotogneri.betfair.models.Tick;

import java.io.IOException;
import java.util.List;

public class StrategySoccerOverUnder05 extends Strategy
{
    private final String marketId;
    private final Session session;
    private final CsvFile logPrice;

    private double maxBackPrice = 0;

    private static final int END_SECOND_HALF = 1000 * 60 * 110; // end of second half (01:50:00)

    public StrategySoccerOverUnder05(Session session, String marketId, List<Long> selections, String logFolderPath) throws IOException
    {
        this.session = session;
        this.marketId = marketId;

        this.logPrice = new CsvFile(logFolderPath + Log.PRICES_LOG_FILE);

        initLogPrice(logPrice, selections);
    }

    private void initLogPrice(CsvFile logPrice, List<Long> selections) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.separator();

        long selectionId = selections.get(1);

        csvLine.append(selectionId + "-back");
        csvLine.append(selectionId + "-lay");

        logPrice.write(csvLine);
    }

    @Override
    public boolean process(Tick tick) throws Exception
    {
        Selection selection = tick.selections.get(1);

        if ((tick.timestamp <= 0) && (selection.back > maxBackPrice))
        {
            maxBackPrice = selection.back;

            CsvLine csvLine = new CsvLine();
            csvLine.appendTimestamp(tick.timestamp);
            csvLine.append(selection.back);
            csvLine.append(selection.lay);

            logPrice.write(csvLine);
        }

        return true;
    }

    @Override
    public void onClose(long timestamp, boolean executed) throws Exception
    {
        if (timestamp > END_SECOND_HALF)
        {
            CsvLine csvLine = new CsvLine();
            csvLine.appendTimestamp(timestamp);
            csvLine.append("0-0 YES " + maxBackPrice);

            logPrice.write(csvLine);
        }
        else
        {
            CsvLine csvLine = new CsvLine();
            csvLine.appendTimestamp(timestamp);
            csvLine.append("0-0 NO  " + maxBackPrice);

            logPrice.write(csvLine);
        }
    }
}