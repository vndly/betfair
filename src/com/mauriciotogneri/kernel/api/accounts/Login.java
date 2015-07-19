package com.mauriciotogneri.kernel.api.accounts;

import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class Login
{
    private final HttpClient httpClient;

    public Login(HttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

    public LoginResponse execute(String username, String password, String appKey) throws IOException
    {
        Request.Builder builder = new Request.Builder();
        builder.url("https://identitysso.betfair.com/api/login");

        builder.addHeader("X-Application", appKey);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.addHeader("Accept", "application/json");

        FormEncodingBuilder parameters = new FormEncodingBuilder();
        parameters.add("username", username);
        parameters.add("password", password);

        builder.post(parameters.build());

        Response response = httpClient.client.newCall(builder.build()).execute();

        return httpClient.gson.fromJson(response.body().charStream(), LoginResponse.class);
    }

    public static class LoginResponse
    {
        public String token = "";
        public String product = "";
        public String status = "";
        public String error = "";

        public boolean isValid()
        {
            return status.equals("SUCCESS");
        }
    }
}