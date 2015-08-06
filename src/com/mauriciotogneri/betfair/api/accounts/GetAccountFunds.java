package com.mauriciotogneri.betfair.api.accounts;

import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.utils.JsonUtils;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class GetAccountFunds
{
    private final HttpClient httpClient;

    public GetAccountFunds(HttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

    public AccountFundsResponse execute(String appKey, String sessionToken) throws IOException
    {
        Request.Builder builder = new Request.Builder();
        builder.url("https://api.betfair.com/exchange/account/rest/v1.0/getAccountFunds/");

        builder.addHeader("X-Application", appKey);
        builder.addHeader("X-Authentication", sessionToken);
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Accept", "application/json");

        MediaType JSON = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(JSON, "{}");
        builder.post(requestBody);

        Response response = httpClient.client.newCall(builder.build()).execute();

        return JsonUtils.fromJson(response.body().string(), AccountFundsResponse.class);
    }

    public static class AccountFundsResponse
    {
        public double availableToBetBalance = 0;
        public double exposure = 0;
        public double retainedCommission = 0;
        public double exposureLimit = 0;
        public double discountRate = 0;
        public int pointsBalance = 0;
    }
}