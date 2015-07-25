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
import com.mauriciotogneri.kernel.models.Selection;
import com.mauriciotogneri.kernel.models.Tick;
import com.mauriciotogneri.kernel.strategies.StrategySoccerOverUnder15;
import com.mauriciotogneri.kernel.utils.NumberFormatter;
import com.mauriciotogneri.kernel.utils.TimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarketMonitor extends AbstractMonitor
{
    private final Event event;
    private final String marketId;
    private final String marketType;
    private final String folderPath;

    private CsvFile logStatus;

    private StrategySoccerOverUnder15 strategy;

    private ListMarketBook listMarketBook = null;
    private long eventStartTime = 0;

    private List<Long> selections = new ArrayList<>();

    private static final int WAITING_TIME = 250; // 4 times per second (in milliseconds)

    public MarketMonitor(HttpClient httpClient, Session session, String folderPath, Event event, MarketCatalogue marketCatalogue)
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

        logStatus = new CsvFile(folderPath + "/status-" + marketType + ".csv");

        listMarketBook = ListMarketBook.getRequest(httpClient, session, marketId);

        MarketBook marketBook = getMarketBook();

        if (marketBook != null)
        {
            for (Runner runner : marketBook.runners)
            {
                selections.add(runner.selectionId);
            }
        }

        strategy = new StrategySoccerOverUnder15(selections, folderPath, marketId, marketType);

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
            Tick tick = new Tick(timestamp);

            for (Long selectionId : selections)
            {
                double back = 0;
                double lay = 0;

                Runner runner = marketBook.getRunner(selectionId);

                if ((runner != null) && (runner.isActive()))
                {
                    PriceSize priceBack = runner.getBackValue();

                    if (priceBack != null)
                    {
                        back = NumberFormatter.round(priceBack.price);
                    }

                    PriceSize priceLay = runner.getLayValue();

                    if (priceLay != null)
                    {
                        lay = NumberFormatter.round(priceLay.price);
                    }
                }

                Selection selection = new Selection(selectionId, back, lay);
                tick.add(selection);
            }

            strategy.process(tick);
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