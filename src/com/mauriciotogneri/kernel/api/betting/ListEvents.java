package com.mauriciotogneri.kernel.api.betting;

import com.mauriciotogneri.kernel.api.base.BettingRequest;
import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.ListCallParameters;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.api.base.Types.EventResult;
import com.mauriciotogneri.kernel.api.base.Types.MarketFilter;
import com.mauriciotogneri.kernel.api.base.Types.MarketFilter.Builder;
import com.mauriciotogneri.kernel.api.betting.ListEvents.Response;

import java.io.IOException;
import java.util.ArrayList;

public class ListEvents extends BettingRequest<Response, ListCallParameters>
{
    public ListEvents(HttpClient httpClient, Session session)
    {
        super(httpClient, session);
    }

    @Override
    protected Class<Response> getClassType()
    {
        return Response.class;
    }

    @Override
    protected String getMethod()
    {
        return "listEvents";
    }

    public static class Response extends ArrayList<EventResult>
    {
    }

    public static ListEvents getRequest(HttpClient httpClient, Session session, boolean inPlay, String... eventTypes) throws IOException
    {
        MarketFilter.Builder marketFilter = new Builder();
        marketFilter.setEventTypeIds(eventTypes);

        if (inPlay)
        {
            marketFilter.setInPlayOnly(true);
        }

        ListCallParameters parameters = new ListCallParameters(marketFilter.build());

        ListEvents listEvents = new ListEvents(httpClient, session);
        listEvents.setParameters(parameters);

        return listEvents;
    }
}