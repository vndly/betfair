package com.mauriciotogneri.kernel.api.base;

import com.mauriciotogneri.kernel.Constants.Debug;
import com.mauriciotogneri.kernel.Constants.Debug.Http;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public abstract class BaseRequest<T, P>
{
    private final HttpClient httpClient;
    private final Session session;

    public BaseRequest(HttpClient httpClient, Session session)
    {
        this.httpClient = httpClient;
        this.session = session;
    }

    protected abstract Class<T> getClassType();

    public T execute(String method, P parameters) throws IOException
    {
        Request.Builder builder = new Request.Builder();
        builder.url("https://api.betfair.com/exchange/betting/rest/v1.0/" + method + "/");

        builder.addHeader("X-Application", session.appKey);
        builder.addHeader("X-Authentication", session.sessionToken);
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Accept", "application/json");

        if (parameters != null)
        {
            MediaType JSON = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(JSON, httpClient.gson.toJson(parameters));
            builder.post(requestBody);
        }

        Response response = httpClient.client.newCall(builder.build()).execute();

        String json = response.body().string();

        if (Debug.ENABLE_LOGS && Http.PRINT_RESPONSE)
        {
            System.out.println(json + "\n");
        }

        return httpClient.gson.fromJson(json, getClassType());
    }

    public T execute(String method) throws IOException
    {
        return execute(method, null);
    }
}