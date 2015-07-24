package com.mauriciotogneri.kernel.monitors;

import com.mauriciotogneri.kernel.api.base.Enums.MarketStatus;
import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.api.base.Types.Event;
import com.mauriciotogneri.kernel.api.base.Types.MarketBook;
import com.mauriciotogneri.kernel.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.kernel.api.base.Types.PriceSize;
import com.mauriciotogneri.kernel.api.base.Types.Runner;
import com.mauriciotogneri.kernel.api.betting.ListMarketBook;
import com.mauriciotogneri.kernel.csv.CsvFile;
import com.mauriciotogneri.kernel.csv.CsvLine;
import com.mauriciotogneri.kernel.utils.NumberFormatter;
import com.mauriciotogneri.kernel.utils.TimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarketMonitorSimple extends AbstractMonitor
{
    private final Event event;
    private final String marketId;
    private final String marketType;
    private final String folderPath;

    private CsvFile logPrice;
    private CsvFile logStatus;

    private ListMarketBook listMarketBook = null;
    private long eventStartTime = 0;

    private List<Long> selections = new ArrayList<>();

    private static final int WAITING_TIME = 1000; // one second (in milliseconds)

    public MarketMonitorSimple(HttpClient httpClient, Session session, String folderPath, Event event, MarketCatalogue marketCatalogue)
    {
        super(httpClient, session);

        this.event = event;
        this.marketId = marketCatalogue.marketId;
        this.marketType = marketCatalogue.description.marketType;
        this.folderPath = folderPath;
    }

    @Override
    protected int getWaitTime()
    {
        return WAITING_TIME;
    }

    @Override
    protected boolean onPreExecute() throws Exception
    {
        eventStartTime = TimeFormatter.dateToMilliseconds(event.openDate, "UTC");

        logPrice = new CsvFile(folderPath + "/" + marketId + "-" + marketType + ".csv");
        logStatus = new CsvFile(folderPath + "/status.csv");

        listMarketBook = ListMarketBook.getRequest(httpClient, session, marketId);

        MarketBook marketBook = getMarketBook();

        if (marketBook != null)
        {
            CsvLine csvLine = new CsvLine();
            csvLine.separator();

            for (Runner runner : marketBook.runners)
            {
                selections.add(runner.selectionId);

                csvLine.append(runner.selectionId + "-back");
                //csvLine.append(runner.selectionId + "-bac-siz");
                csvLine.append(runner.selectionId + "-lay");
                //csvLine.append(runner.selectionId + "-lay-siz");
                csvLine.append(runner.selectionId + "-diff");
            }

            logPrice.write(csvLine);
        }

        return (marketBook != null);
    }

    @Override
    protected boolean execute() throws Exception
    {
        MarketBook marketBook = getMarketBook();

        long timestamp = (System.currentTimeMillis() - eventStartTime);

        if (marketBook != null)
        {
            CsvLine csvLine = new CsvLine();
            csvLine.appendTimestamp(timestamp);
            csvLine.append(marketBook.status.toString());

            logStatus.write(csvLine);
        }

        if ((marketBook == null) || (marketBook.status == MarketStatus.CLOSED))
        {
            return false;
        }

        if (marketBook.status == MarketStatus.OPEN)
        {
            CsvLine csvLine = new CsvLine();
            csvLine.appendTimestamp(timestamp);

            for (Long selectionId : selections)
            {
                PriceSize priceBack = null;
                PriceSize priceLay = null;

                Runner runner = marketBook.getRunner(selectionId);

                if ((runner != null) && (runner.isActive()))
                {
                    priceBack = runner.getBackValue();
                    priceLay = runner.getLayValue();
                }

                if (priceBack != null)
                {
                    csvLine.append(NumberFormatter.round(priceBack.price, 3));
                }
                else
                {
                    csvLine.append(0);
                }

                if (priceLay != null)
                {
                    csvLine.append(NumberFormatter.round(priceLay.price, 3));
                }
                else
                {
                    csvLine.append(0);
                }

                if ((priceBack != null) && (priceLay != null))
                {
                    csvLine.append(NumberFormatter.round(priceLay.price - priceBack.price, 3));
                }
                else
                {
                    csvLine.append(0);
                }
            }

            logPrice.write(csvLine);
        }

        return true;
    }

    private MarketBook getMarketBook() throws IOException
    {
        ListMarketBook.Response marketBookResponse = listMarketBook.execute();

        if (!marketBookResponse.isEmpty())
        {
            return marketBookResponse.get(0);
        }

        return null;
    }
}