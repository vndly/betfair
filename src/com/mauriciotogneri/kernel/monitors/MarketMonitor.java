package com.mauriciotogneri.kernel.monitors;

import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.api.base.Types.Event;
import com.mauriciotogneri.kernel.api.base.Types.MarketBook;
import com.mauriciotogneri.kernel.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.kernel.api.base.Types.Runner;
import com.mauriciotogneri.kernel.api.betting.ListMarketBook;
import com.mauriciotogneri.kernel.processors.EventProcessor;
import com.mauriciotogneri.kernel.utils.LogWriter;
import com.mauriciotogneri.kernel.utils.TimeFormatter;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

import java.io.IOException;

public class MarketMonitor extends AbstractMonitor
{
    private final Event event;
    private final EventProcessor eventProcessor;
    private final String marketId;
    private final String marketType;

    private LogWriter logWriter;
    private PeriodFormatter periodFormatter;

    private ListMarketBook listMarketBook = null;
    private long eventStartTime = 0;

    private static final String SEPARATOR = ";";
    private static final int WAITING_TIME = 1 * 1000; // one second (in milliseconds)

    public MarketMonitor(HttpClient httpClient, Session session, Event event, MarketCatalogue marketCatalogue, EventProcessor eventProcessor)
    {
        super(httpClient, session);

        this.event = event;
        this.eventProcessor = eventProcessor;
        this.marketId = marketCatalogue.marketId;
        this.marketType = marketCatalogue.description.marketType;
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

        logWriter = new LogWriter("logs/markets/" + event.id + "/" + marketId + ".log");

        periodFormatter = TimeFormatter.getPeriodFormatter();

        listMarketBook = ListMarketBook.getRequest(httpClient, session, marketId);

        MarketBook marketBook = getMarketBook();

        if (marketBook != null)
        {
            StringBuilder builder = new StringBuilder();

            for (Runner runner : marketBook.runners)
            {
                builder.append(SEPARATOR).append(runner.selectionId).append("-min");
                builder.append(SEPARATOR).append(runner.selectionId).append("-max");
                builder.append(SEPARATOR).append(runner.selectionId).append("-avg");
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

        if (marketBook == null)
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
                double minimumPrice = runner.ex.getMinimumPrice();
                double maximumPrice = runner.ex.getMaximumPrice();
                double averagePrice = runner.ex.getAveragePrice();

                builder.append(SEPARATOR).append(String.valueOf(minimumPrice));
                builder.append(SEPARATOR).append(String.valueOf(maximumPrice));
                builder.append(SEPARATOR).append(String.valueOf(averagePrice));
            }
        }

        logWriter.write(builder.toString());

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