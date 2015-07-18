package com.mauriciotogneri.kernel.api;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class Login
{
    private final OkHttpClient client;
    private final Gson gson;

    public Login(OkHttpClient client, Gson gson)
    {
        this.client = client;
        this.gson = gson;
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

        Response response = client.newCall(builder.build()).execute();

        return gson.fromJson(response.body().charStream(), LoginResponse.class);
    }

    public static class LoginResponse
    {
        private String token = "";
        private String product = "";
        private String status = "";
        private String error = "";

        public boolean isValid()
        {
            return status.equals("SUCCESS");
        }

        public String getToken()
        {
            return token;
        }
    }
}