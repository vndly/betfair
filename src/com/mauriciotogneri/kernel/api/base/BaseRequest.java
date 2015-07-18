package com.mauriciotogneri.kernel.api.base;

import com.google.gson.Gson;
import com.mauriciotogneri.kernel.Main;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public abstract class BaseRequest<T, P>
{
    private final OkHttpClient client;
    private final Gson gson;
    private final String appKey;
    private final String sessionToken;

    public BaseRequest(OkHttpClient client, Gson gson, String appKey, String sessionToken)
    {
        this.client = client;
        this.gson = gson;
        this.appKey = appKey;
        this.sessionToken = sessionToken;
    }

    protected abstract Class<T> getClassType();

    public T execute(String method, P parameters) throws IOException
    {
        Request.Builder builder = new Request.Builder();
        builder.url("https://api.betfair.com/exchange/betting/rest/v1.0/" + method + "/");

        builder.addHeader("X-Application", appKey);
        builder.addHeader("X-Authentication", sessionToken);
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Accept", "application/json");

        if (parameters != null)
        {
            MediaType JSON = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(JSON, gson.toJson(parameters));
            builder.post(requestBody);

            if (Main.ENABLE_LOGS)
            {
                System.out.println(gson.toJson(parameters));
            }
        }

        Response response = client.newCall(builder.build()).execute();

        String json = response.body().string();

        if (Main.ENABLE_LOGS)
        {
            System.out.println(json);
        }

        return gson.fromJson(json, getClassType());
    }

    public T execute(String method) throws IOException
    {
        return execute(method, null);
    }
}