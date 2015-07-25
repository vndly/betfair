package com.mauriciotogneri.kernel.api.base;

import com.mauriciotogneri.kernel.Constants.Debug;
import com.mauriciotogneri.kernel.Constants.Debug.Http;
import com.mauriciotogneri.kernel.utils.JsonUtils;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public abstract class BettingRequest<T, P>
{
    private final HttpClient httpClient;
    private final Session session;
    private P parameters;

    public BettingRequest(HttpClient httpClient, Session session)
    {
        this.httpClient = httpClient;
        this.session = session;
    }

    protected abstract Class<T> getClassType();

    protected abstract String getMethod();

    public synchronized void setParameters(P parameters)
    {
        this.parameters = parameters;
    }

    private synchronized T executeRequest(P parameters) throws IOException
    {
        Request.Builder builder = new Request.Builder();
        builder.url("https://api.betfair.com/exchange/betting/rest/v1.0/" + getMethod() + "/");

        builder.addHeader("X-Application", session.appKey);
        builder.addHeader("X-Authentication", session.getSessionToken());
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Accept", "application/json");

        if (parameters != null)
        {
            MediaType JSON = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(JSON, JsonUtils.toJson(parameters));
            builder.post(requestBody);
        }

        Response response = httpClient.client.newCall(builder.build()).execute();

        String json = response.body().string();

        if (Debug.ENABLE_LOGS && Http.PRINT_RESPONSE)
        {
            System.out.println(json + "\n");
        }

        return JsonUtils.fromJson(json, getClassType());
    }

    public synchronized T execute(P parameters) throws IOException
    {
        return executeRequest(parameters);
    }

    public synchronized T execute() throws IOException
    {
        return executeRequest(parameters);
    }
}