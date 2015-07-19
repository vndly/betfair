package com.mauriciotogneri.kernel.api.processors;

import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.api.base.Types.MarketBook;
import com.mauriciotogneri.kernel.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.kernel.api.betting.ListMarketBook;

public class MarketAnalyzer extends Thread
{
    private final HttpClient httpClient;
    private final Session session;
    private final String marketId;
    private final String marketType;

    private volatile boolean finished = false;

    public MarketAnalyzer(HttpClient httpClient, Session session, MarketCatalogue marketCatalogue)
    {
        this.httpClient = httpClient;
        this.session = session;
        this.marketId = "1"; //marketCatalogue.marketId;
        this.marketType = marketCatalogue.description.marketType;
    }

    @Override
    public void run()
    {
        while (!finished)
        {
            threadSleep(1000);

            try
            {
                MarketBook marketBook = ListMarketBook.fromMarketId(httpClient, session, marketId);

                if (marketBook != null)
                {
                    System.out.println(httpClient.gson.toJson(marketBook));

                    System.out.println(marketBook.version);
                }
                else
                {
                    finished = true;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();

                finished = true;
            }
        }
    }

    private void threadSleep(long milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}