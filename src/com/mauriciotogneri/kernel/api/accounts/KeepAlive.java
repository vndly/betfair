package com.mauriciotogneri.kernel.api.accounts;

import com.mauriciotogneri.kernel.api.accounts.Login.LoginResponse;
import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class KeepAlive
{
    private final HttpClient httpClient;

    public KeepAlive(HttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

    public LoginResponse execute(String appKey, String sessionToken) throws IOException
    {
        Request.Builder builder = new Request.Builder();
        builder.url("https://identitysso.betfair.com/api/keepAlive");

        builder.addHeader("X-Application", appKey);
        builder.addHeader("X-Authentication", sessionToken);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.addHeader("Accept", "application/json");

        Response response = httpClient.client.newCall(builder.build()).execute();

        return httpClient.gson.fromJson(response.body().charStream(), LoginResponse.class);
    }
}