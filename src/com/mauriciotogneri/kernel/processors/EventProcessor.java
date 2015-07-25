package com.mauriciotogneri.kernel.processors;

import com.mauriciotogneri.kernel.api.base.Enums.MarketTypeEnum;
import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.api.base.Types.Event;
import com.mauriciotogneri.kernel.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.kernel.api.betting.ListMarketCatalogue;
import com.mauriciotogneri.kernel.monitors.MarketMonitor;

import java.io.IOException;

public class EventProcessor
{
    private final Event event;

    public EventProcessor(Event event)
    {
        this.event = event;
    }

    public void process(HttpClient httpClient, Session session, String folderPath) throws IOException
    {
        ListMarketCatalogue.Response response = ListMarketCatalogue.get(httpClient, session, event.id, MarketTypeEnum.MATCH_ODDS.toString());

        for (MarketCatalogue marketCatalogue : response)
        {
            MarketMonitor marketMonitor = new MarketMonitor(HttpClient.getDefault(), session, folderPath, event, marketCatalogue);
            marketMonitor.start();
        }
    }
}