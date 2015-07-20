package com.mauriciotogneri.kernel.processors;

import com.mauriciotogneri.kernel.api.base.Enums.MarketTypeEnum;
import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.api.base.Types.Event;
import com.mauriciotogneri.kernel.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.kernel.api.betting.ListMarketCatalogue;
import com.mauriciotogneri.kernel.monitors.EventMonitor;
import com.mauriciotogneri.kernel.monitors.MarketMonitor;

import java.io.IOException;

public class EventProcessor
{
    private final EventMonitor eventMonitor;
    private final Event event;
    private int marketsCount = 0;

    public EventProcessor(EventMonitor eventMonitor, Event event)
    {
        this.eventMonitor = eventMonitor;
        this.event = event;
    }

    public void process(HttpClient httpClient, Session session) throws IOException
    {
        ListMarketCatalogue.Response response = ListMarketCatalogue.get(httpClient, session, event.id, MarketTypeEnum.MATCH_ODDS.toString());

        for (MarketCatalogue marketCatalogue : response)
        {
            incrementMarket();

            MarketMonitor marketProcessor = new MarketMonitor(HttpClient.getDefault(), session, event, marketCatalogue, this);
            marketProcessor.start();
        }
    }

    private synchronized void incrementMarket()
    {
        marketsCount++;
    }

    public synchronized void decrementMarket()
    {
        marketsCount--;

        if (marketsCount <= 0)
        {
            eventMonitor.removeEvent(event.id);
        }
    }
}