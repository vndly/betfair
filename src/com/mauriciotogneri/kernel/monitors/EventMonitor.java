package com.mauriciotogneri.kernel.monitors;

import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.api.base.Types.Event;
import com.mauriciotogneri.kernel.api.base.Types.EventResult;
import com.mauriciotogneri.kernel.api.betting.ListEvents;
import com.mauriciotogneri.kernel.processors.EventProcessor;
import com.mauriciotogneri.kernel.utils.IoUtils;
import com.mauriciotogneri.kernel.utils.JsonUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EventMonitor extends AbstractMonitor
{
    private final String[] eventType;
    private ListEvents listEvents;
    private final Set<String> eventsSet = new HashSet<>();

    private static final int WAITING_TIME = 60 * 1000; // one minute (in milliseconds)

    public EventMonitor(HttpClient httpClient, Session session, String... eventType)
    {
        super(httpClient, session);

        this.eventType = eventType;
    }

    @Override
    protected int getWaitTime()
    {
        return WAITING_TIME;
    }

    @Override
    protected boolean onPreExecute() throws Exception
    {
        listEvents = ListEvents.getRequest(httpClient, session, eventType);

        return true;
    }

    public synchronized boolean eventExists(String eventId)
    {
        return eventsSet.contains(eventId);
    }

    public synchronized void addEvent(String eventId)
    {
        if (!eventExists(eventId))
        {
            eventsSet.add(eventId);
        }
    }

    public synchronized void removeEvent(String eventId)
    {
        if (eventExists(eventId))
        {
            eventsSet.remove(eventId);
        }
    }

    @Override
    protected boolean execute() throws Exception
    {
        ListEvents.Response listEventsResponse = listEvents.execute();

        for (EventResult eventResult : listEventsResponse)
        {
            Event event = eventResult.event;

            if (!eventExists(event.id))
            {
                addEvent(event.id);

                EventProcessor eventProcessor = new EventProcessor(this, event);
                eventProcessor.process(httpClient, session);

                logEvent(event);
            }
        }

        return true;
    }

    private void logEvent(Event event)
    {
        try
        {
            IoUtils.writeToFile("logs/events/" + event.id + ".log", JsonUtils.toJson(event));
        }
        catch (IOException e)
        {
            // TODO
        }
    }
}