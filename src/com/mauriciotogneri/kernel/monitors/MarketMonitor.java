package com.mauriciotogneri.kernel.monitors;

import com.mauriciotogneri.kernel.Constants;
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
import com.mauriciotogneri.kernel.strategies.Strategy;
import com.mauriciotogneri.kernel.utils.IoUtils;
import com.mauriciotogneri.kernel.utils.JsonUtils;
import com.mauriciotogneri.kernel.utils.NumberUtils;
import com.mauriciotogneri.kernel.utils.TimeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarketMonitor extends AbstractMonitor
{
    private final Event event;
    private final String eventType;
    private final String marketId;
    private final String marketType;
    private final MarketCatalogue marketCatalogue;
    private final String logFolderPath;

    private CsvFile logStatus;

    private Strategy strategy;

    private ListMarketBook listMarketBook = null;
    private long eventStartTime = 0;

    private final List<Long> selections = new ArrayList<>();

    private static final int WAITING_TIME = 250; // 4 times per second (in milliseconds)

    public MarketMonitor(HttpClient httpClient, Session session, String logFolderPath, Event event, String eventType, MarketCatalogue marketCatalogue)
    {
        super(httpClient, session);

        this.event = event;
        this.eventType = eventType;
        this.marketId = marketCatalogue.marketId;
        this.marketType = marketCatalogue.description.marketType;
        this.marketCatalogue = marketCatalogue;
        this.logFolderPath = logFolderPath;
    }

    @Override
    protected int getWaitTime()
    {
        return WAITING_TIME;
    }

    @Override
    protected boolean onPreExecute() throws Exception
    {
        IoUtils.writeFile(logFolderPath + Constants.Log.INFO_LOG_FILE, JsonUtils.toJson(marketCatalogue));

        eventStartTime = TimeUtils.dateToMilliseconds(event.openDate, "UTC");

        logStatus = new CsvFile(logFolderPath + Constants.Log.STATUS_LOG_FILE);

        listMarketBook = ListMarketBook.getRequest(httpClient, session, marketId);

        MarketBook marketBook = getMarketBook();

        if (marketBook != null)
        {
            for (Runner runner : marketBook.runners)
            {
                selections.add(runner.selectionId);
            }
        }

        strategy = Strategy.getStrategy(eventType, marketType, marketId, selections, logFolderPath);

        return (marketBook != null) && (strategy != null) && (strategy.isValid(System.currentTimeMillis() - eventStartTime));
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
                        back = NumberUtils.round(priceBack.price);
                    }

                    PriceSize priceLay = runner.getLayValue();

                    if (priceLay != null)
                    {
                        lay = NumberUtils.round(priceLay.price);
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