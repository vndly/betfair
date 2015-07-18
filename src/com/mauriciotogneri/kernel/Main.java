package com.mauriciotogneri.kernel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mauriciotogneri.kernel.api.ListEvents;
import com.mauriciotogneri.kernel.api.ListMarketCatalogue;
import com.mauriciotogneri.kernel.api.Login;
import com.mauriciotogneri.kernel.api.base.Enums.EventTypeEnum;
import com.mauriciotogneri.kernel.api.base.Enums.MarketProjection;
import com.mauriciotogneri.kernel.api.base.ListCallParameters;
import com.mauriciotogneri.kernel.api.base.Types.MarketFilter;
import com.mauriciotogneri.kernel.api.base.Types.MarketFilter.Builder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class Main
{
    public static final boolean ENABLE_LOGS = true;

    public static void main(String[] args) throws Exception
    {
        String username = args[0];
        String password = args[1];
        String appKey = args[2];

        OkHttpClient client = new OkHttpClient();

        if (ENABLE_LOGS)
        {
            client.interceptors().add(new Interceptor()
            {
                @Override
                public Response intercept(Chain chain) throws IOException
                {
                    Request request = chain.request();

                    long t1 = System.nanoTime();
                    System.out.println(String.format("Sending request %s%n%s", request.url(), request.headers()));

                    Response response = chain.proceed(request);

                    long t2 = System.nanoTime();
                    System.out.println(String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));

                    return response;
                }
            });
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Main main = new Main();
        main.init(client, gson, username, password, appKey);
    }

    private void init(OkHttpClient client, Gson gson, String username, String password, String appKey) throws IOException
    {
        Login login = new Login(client, gson);
        //LoginResponse loginResponse = login.execute(username, password, appKey);

        String sessionToken = "korOWiFERGy2O1NArOl4NSyJJQQpAlxonrt8cm7vi1M=";

        if (true)
        {
            //ListEvents.Response listEventsResponse = getSoccerEvents(client, gson, appKey, sessionToken);

            //System.out.println(gson.toJson(listEventsResponse));

            //processEvents(client, gson, appKey, sessionToken, listEventsResponse);

            getEvents(client, gson, appKey, sessionToken);
        }
    }

    private ListEvents.Response getSoccerEvents(OkHttpClient client, Gson gson, String appKey, String sessionToken) throws IOException
    {
        MarketFilter.Builder marketFilter = new Builder();
        marketFilter.setEventTypeIds(EventTypeEnum.SOCCER.toString());
        marketFilter.setInPlayOnly(true);

        ListCallParameters parameters = new ListCallParameters(marketFilter.build());

        ListEvents listEvents = new ListEvents(client, gson, appKey, sessionToken);

        return listEvents.execute(parameters);
    }

    private void processEvents(OkHttpClient client, Gson gson, String appKey, String sessionToken, ListEvents.Response events) throws IOException
    {
        if (!events.isEmpty())
        {
            String eventId = events.get(0).event.id;

            processEvent(client, gson, appKey, sessionToken, eventId);
        }
    }

    private void processEvent(OkHttpClient client, Gson gson, String appKey, String sessionToken, String eventId) throws IOException
    {
        MarketFilter.Builder marketFilter = new Builder();
        marketFilter.setEventIds(eventId);
        marketFilter.setMarketTypeCodes("MATCH_ODDS");

        ListMarketCatalogue.Parameters.Builder parameters = new ListMarketCatalogue.Parameters.Builder(marketFilter.build());
        parameters.setMarketProjection(MarketProjection.RUNNER_DESCRIPTION, MarketProjection.RUNNER_METADATA);
        parameters.setMaxResults(1000);

        ListMarketCatalogue listMarketCatalogue = new ListMarketCatalogue(client, gson, appKey, sessionToken);
        ListMarketCatalogue.Response response = listMarketCatalogue.execute(parameters.build());

        System.out.println(gson.toJson(response));
    }

    private void getEvents(OkHttpClient client, Gson gson, String appKey, String sessionToken) throws IOException
    {
        MarketFilter.Builder marketFilter = new Builder();
        marketFilter.setEventTypeIds(EventTypeEnum.SOCCER.toString());
        marketFilter.setInPlayOnly(true);
        marketFilter.setMarketTypeCodes("MATCH_ODDS");

        ListMarketCatalogue.Parameters.Builder parameters = new ListMarketCatalogue.Parameters.Builder(marketFilter.build());
        parameters.setMarketProjection(MarketProjection.EVENT);
        parameters.setMaxResults(1000);

        ListMarketCatalogue listMarketCatalogue = new ListMarketCatalogue(client, gson, appKey, sessionToken);
        ListMarketCatalogue.Response response = listMarketCatalogue.execute(parameters.build());

        System.out.println(gson.toJson(response));
        System.out.println(response.size());
    }
}