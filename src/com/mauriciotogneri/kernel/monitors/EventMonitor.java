package com.mauriciotogneri.kernel.monitors;

import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.api.base.Types.Event;
import com.mauriciotogneri.kernel.api.base.Types.EventResult;
import com.mauriciotogneri.kernel.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.kernel.api.betting.ListEvents;
import com.mauriciotogneri.kernel.api.betting.ListMarketCatalogue;
import com.mauriciotogneri.kernel.logs.ErrorLog;
import com.mauriciotogneri.kernel.utils.IoUtils;
import com.mauriciotogneri.kernel.utils.JsonUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EventMonitor extends AbstractMonitor
{
    private final String eventType;
    private final boolean inPlay;
    private ListEvents listEvents;
    private final String[] marketTypes;
    private final Set<String> eventsSet = new HashSet<>();

    private static final int WAITING_TIME = 60 * 1000; // one minute (in milliseconds)

    public EventMonitor(HttpClient httpClient, Session session, String eventType, boolean inPlay, String[] marketTypes)
    {
        super(httpClient, session);

        this.eventType = eventType;
        this.inPlay = inPlay;
        this.marketTypes = marketTypes;
    }

    @Override
    protected int getWaitTime()
    {
        return WAITING_TIME;
    }

    @Override
    protected boolean onPreExecute() throws Exception
    {
        listEvents = ListEvents.getRequest(httpClient, session, inPlay, eventType);

        return true;
    }

    private synchronized boolean eventExists(String eventId)
    {
        return eventsSet.contains(eventId);
    }

    private synchronized boolean addEvent(String eventId)
    {
        if (!eventExists(eventId))
        {
            eventsSet.add(eventId);

            return true;
        }

        return false;
    }

    @Override
    protected boolean execute() throws Exception
    {
        ListEvents.Response listEventsResponse = listEvents.execute();

        for (EventResult eventResult : listEventsResponse)
        {
            Event event = eventResult.event;

            if (addEvent(event.id))
            {
                String folderPath = "logs/events/" + eventType + "/" + event.id;

                ListMarketCatalogue.Response marketCatalogueResponse = ListMarketCatalogue.get(HttpClient.getDefault(), session, event.id, marketTypes);

                for (MarketCatalogue marketCatalogue : marketCatalogueResponse)
                {
                    MarketMonitor marketMonitor = new MarketMonitor(HttpClient.getDefault(), session, folderPath, event, marketCatalogue);
                    marketMonitor.start();
                }

                logEvent(event, folderPath);
            }
        }

        return true;
    }

    private void logEvent(Event event, String folderPath)
    {
        try
        {
            IoUtils.writeFile(folderPath + "/info.log", JsonUtils.toJson(event));
        }
        catch (IOException e)
        {
            ErrorLog.log(e);
        }
    }
}