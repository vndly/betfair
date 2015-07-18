package com.mauriciotogneri.kernel.api;

import com.google.gson.Gson;
import com.mauriciotogneri.kernel.api.base.BaseRequest;
import com.mauriciotogneri.kernel.api.base.ListCallParameters;
import com.mauriciotogneri.kernel.api.base.Types.EventResult;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;

public class ListEvents extends BaseRequest<ListEvents.Response, ListCallParameters>
{
    public ListEvents(OkHttpClient client, Gson gson, String appKey, String sessionToken)
    {
        super(client, gson, appKey, sessionToken);
    }

    @Override
    protected Class<Response> getClassType()
    {
        return Response.class;
    }

    public Response execute() throws IOException
    {
        return execute(new ListCallParameters());
    }

    public Response execute(ListCallParameters parameters) throws IOException
    {
        return execute("listEvents", parameters);
    }

    public static class Response extends ArrayList<EventResult>
    {
    }
}