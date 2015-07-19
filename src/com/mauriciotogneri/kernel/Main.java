package com.mauriciotogneri.kernel;

import com.mauriciotogneri.kernel.api.base.Enums.EventTypeEnum;
import com.mauriciotogneri.kernel.api.base.Enums.MarketTypeEnum;
import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.api.base.Types.Event;
import com.mauriciotogneri.kernel.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.kernel.api.betting.ListEvents;
import com.mauriciotogneri.kernel.api.betting.ListMarketCatalogue;
import com.mauriciotogneri.kernel.api.processors.MarketAnalyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Main main = new Main();
        main.init(args[0]);
    }

    private void init(String configFilePath) throws IOException
    {
        InputStream input = new FileInputStream(configFilePath);
        Properties properties = new Properties();
        properties.load(input);
        input.close();

        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String appKey = properties.getProperty("appkey");

        HttpClient httpClient = HttpClient.getDefault();

        run(httpClient, username, password, appKey);
    }

    private void run(HttpClient httpClient, String username, String password, String appKey) throws IOException
    {
        //Login login = new Login(httpClient);
        //LoginResponse loginResponse = login.execute(username, password, appKey);

        Session session = new Session(appKey, "uHkgiyPrfKvS28q69Dj0+HSz3ZmYtUFqVJVYhTEzjWE=");

        //KeepAlive keepAlive = new KeepAlive(httpClient);
        //LoginResponse keepAliveResponse = keepAlive.execute(appKey, session.sessionToken);

        if (true)
        {
            ListEvents.Response listEventsResponse = ListEvents.get(httpClient, session, EventTypeEnum.SOCCER.toString());

            System.out.println(httpClient.gson.toJson(listEventsResponse));
            System.out.println(listEventsResponse.size());

            processEvents(httpClient, session, listEventsResponse);
        }
    }

    private void processEvents(HttpClient httpClient, Session session, ListEvents.Response events) throws IOException
    {
        if (!events.isEmpty())
        {
            processEvent(httpClient, session, events.get(0).event);

            //            for (EventResult eventResult : events)
            //            {
            //                processEvent(httpClient, session, eventResult.event);
            //            }
        }
    }

    private void processEvent(HttpClient httpClient, Session session, Event event) throws IOException
    {
        ListMarketCatalogue.Response response = ListMarketCatalogue.get(httpClient, session, event.id, MarketTypeEnum.MATCH_ODDS.toString());

        System.out.println(httpClient.gson.toJson(response));

        for (MarketCatalogue marketCatalogue : response)
        {
            processMarket(session, event, marketCatalogue);
        }
    }

    private void processMarket(Session session, Event event, MarketCatalogue marketCatalogue)
    {
        MarketAnalyzer marketAnalyzer = new MarketAnalyzer(HttpClient.getDefault(), session, event, marketCatalogue);
        marketAnalyzer.start();
    }
}