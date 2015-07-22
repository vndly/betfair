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
import com.mauriciotogneri.kernel.processors.EventProcessor;
import com.mauriciotogneri.kernel.utils.LogWriter;
import com.mauriciotogneri.kernel.utils.NumberFormatter;
import com.mauriciotogneri.kernel.utils.TimeFormatter;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

import java.io.IOException;
import java.util.List;

public class MarketMonitorSimple extends AbstractMonitor
{
    private final Event event;
    private final EventProcessor eventProcessor;
    private final String marketId;
    private final String marketType;
    private final String folderPath;

    private LogWriter logWriter;
    private PeriodFormatter periodFormatter;

    private ListMarketBook listMarketBook = null;
    private long eventStartTime = 0;

    private static final String SEPARATOR = ";";
    private static final int WAITING_TIME = 1000; // one second (in milliseconds)

    public MarketMonitorSimple(HttpClient httpClient, Session session, String folderPath, Event event, MarketCatalogue marketCatalogue, EventProcessor eventProcessor)
    {
        super(httpClient, session);

        this.event = event;
        this.eventProcessor = eventProcessor;
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

        logWriter = new LogWriter(folderPath + "/" + marketId + "-" + marketType + ".csv");

        periodFormatter = TimeFormatter.getPeriodFormatter();

        listMarketBook = ListMarketBook.getRequest(httpClient, session, marketId);

        MarketBook marketBook = getMarketBook();

        if (marketBook != null)
        {
            StringBuilder builder = new StringBuilder();

            for (Runner runner : marketBook.runners)
            {
                builder.append(SEPARATOR).append(runner.selectionId).append("-bac-pri");
                builder.append(SEPARATOR).append(runner.selectionId).append("-bac-siz");
                builder.append(SEPARATOR).append(runner.selectionId).append("-lay-pri");
                builder.append(SEPARATOR).append(runner.selectionId).append("-lay-siz");
            }

            logWriter.write(builder.toString());
        }

        return (marketBook != null);
    }

    @Override
    protected void onPostExecute()
    {
        eventProcessor.decrementMarket();
    }

    @Override
    protected boolean execute() throws Exception
    {
        MarketBook marketBook = getMarketBook();

        if ((marketBook == null) || (marketBook.status == MarketStatus.CLOSED))
        {
            return false;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("\n");

        long currentTime = (System.currentTimeMillis() - eventStartTime);
        Period period = new Period(currentTime);
        builder.append(periodFormatter.print(period));

        for (Runner runner : marketBook.runners)
        {
            if (runner.isActive())
            {
                List<PriceSize> availableToBack = runner.ex.availableToBack;

                for (PriceSize priceSize : availableToBack)
                {
                    builder.append(SEPARATOR).append(NumberFormatter.round(priceSize.price, 3));
                    builder.append(SEPARATOR).append(NumberFormatter.round(priceSize.size, 3));
                }

                List<PriceSize> availableToLay = runner.ex.availableToLay;

                for (PriceSize priceSize : availableToLay)
                {
                    builder.append(SEPARATOR).append(NumberFormatter.round(priceSize.price, 3));
                    builder.append(SEPARATOR).append(NumberFormatter.round(priceSize.size, 3));
                }
            }
        }

        if (!marketBook.runners.isEmpty())
        {
            logWriter.write(builder.toString());
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