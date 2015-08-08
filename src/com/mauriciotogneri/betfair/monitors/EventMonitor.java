package com.mauriciotogneri.betfair.monitors;

import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.api.base.Enums.EventTypeEnum;
import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.api.base.Types.Event;
import com.mauriciotogneri.betfair.api.base.Types.EventResult;
import com.mauriciotogneri.betfair.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.betfair.api.betting.ListEvents;
import com.mauriciotogneri.betfair.api.betting.ListMarketCatalogue;
import com.mauriciotogneri.betfair.logs.ActivityLog;
import com.mauriciotogneri.betfair.logs.ErrorLog;
import com.mauriciotogneri.betfair.utils.IoUtils;
import com.mauriciotogneri.betfair.utils.JsonUtils;

import java.io.IOException;
import java.util.Arrays;
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

    @Override
    protected void onPostExecute(boolean executed) throws Exception
    {
        StringBuilder builder = new StringBuilder();
        builder.append("FINISHED EVENT MONITOR FOR ");
        builder.append("TYPE: ").append(EventTypeEnum.get(eventType)).append(" - ");
        builder.append("IN PLAY: ").append(inPlay).append(" - ");
        builder.append("MARKETS: ").append(Arrays.toString(marketTypes));

        ActivityLog.log(builder.toString());
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

        ActivityLog.log("PROCESSING EVENTS: " + listEventsResponse.size());

        for (EventResult eventResult : listEventsResponse)
        {
            Event event = eventResult.event;

            if (addEvent(event.id))
            {
                String logFolderPath = Log.EVENT_LOG_PATH + eventType + "/" + event.id + "/";

                ListMarketCatalogue.Response marketCatalogueResponse = ListMarketCatalogue.get(HttpClient.getDefault(), session, event.id, marketTypes);

                for (MarketCatalogue marketCatalogue : marketCatalogueResponse)
                {
                    MarketMonitor marketMonitor = new MarketMonitor(HttpClient.getDefault(), session, logFolderPath + marketCatalogue.marketId + "/", event, eventType, marketCatalogue);
                    marketMonitor.start();

                    ActivityLog.log("STARTING MARKET MONITOR FOR: " + JsonUtils.toJson(event, false) + " - " + JsonUtils.toJson(marketCatalogue, false));
                }

                logEvent(event, logFolderPath);
            }
        }

        return isRunning();
    }

    private void logEvent(Event event, String logFolderPath)
    {
        try
        {
            IoUtils.writeFile(logFolderPath + Log.INFO_LOG_FILE, JsonUtils.toJson(event));
        }
        catch (IOException e)
        {
            ErrorLog.log(e);
        }
    }
}