package com.mauriciotogneri.kernel.api.processors;

import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.api.base.Types.MarketBook;
import com.mauriciotogneri.kernel.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.kernel.api.base.Types.Runner;
import com.mauriciotogneri.kernel.api.betting.ListMarketBook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MarketAnalyzer extends Thread
{
    private final HttpClient httpClient;
    private final Session session;

    private final String eventId;
    private final String marketId;
    private final String marketType;

    private static final String SEPARATOR = ";";
    private static final int TIME = 1000;
    private static final int LOOP_LIMIT = 20;

    private volatile boolean finished = false;

    public MarketAnalyzer(HttpClient httpClient, Session session, String eventId, MarketCatalogue marketCatalogue)
    {
        this.httpClient = httpClient;
        this.session = session;

        this.eventId = eventId;
        this.marketId = marketCatalogue.marketId;
        this.marketType = marketCatalogue.description.marketType;
    }

    @Override
    public void run()
    {
        long startTime = System.currentTimeMillis();

        Set<String> selections = new HashSet<>();
        List<DataRow> rows = new ArrayList<>();

        int index = 0;

        while (!finished)
        {
            try
            {
                long currentTime = (System.currentTimeMillis() - startTime) / 1000;

                MarketBook marketBook = ListMarketBook.fromMarketId(httpClient, session, marketId);

                if (marketBook != null)
                {
                    //System.out.println(httpClient.gson.toJson(marketBook));

                    Map<String, Double> values = new HashMap<>();

                    for (Runner runner : marketBook.runners)
                    {
                        if (runner.isActive())
                        {
                            //StringBuilder builder = new StringBuilder();
                            //builder.append(currentTime).append(SEPARATOR);
                            //builder.append(runner.selectionId);
                            //builder.append(SEPARATOR).append(runner.ex.getMinimumPrice());
                            //System.out.println(builder.toString());

                            double minimumPrice = runner.ex.getMinimumPrice();
                            double maximumPrice = runner.ex.getMaximumPrice();
                            double averagePrice = runner.ex.getAveragePrice();

                            if (!Double.isNaN(minimumPrice))
                            {
                                String selectionId = runner.selectionId + "-min";
                                values.put(selectionId, minimumPrice);
                                selections.add(selectionId);
                            }

                            if (!Double.isNaN(maximumPrice))
                            {
                                String selectionId = runner.selectionId + "-max";
                                values.put(selectionId, maximumPrice);
                                selections.add(selectionId);
                            }

                            if (!Double.isNaN(averagePrice))
                            {
                                String selectionId = runner.selectionId + "-avg";
                                values.put(selectionId, averagePrice);
                                selections.add(selectionId);
                            }
                        }
                    }

                    if (!values.isEmpty())
                    {
                        DataRow dataRow = new DataRow(currentTime, values);
                        rows.add(dataRow);
                    }
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

            if (!finished)
            {
                threadSleep(TIME);

                finished = (++index) >= LOOP_LIMIT;
            }
        }

        printResult(selections, rows);
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

    private void printResult(Set<String> selections, List<DataRow> rows)
    {
        StringBuilder builder = new StringBuilder();

        for (String selection : selections)
        {
            builder.append(SEPARATOR).append(selection);
        }

        for (DataRow dataRow : rows)
        {
            builder.append("\n");
            builder.append(dataRow.timestamp);

            for (String selection : selections)
            {
                builder.append(SEPARATOR);

                if (dataRow.values.containsKey(selection))
                {
                    double value = dataRow.values.get(selection);

                    builder.append(Math.floor(value * 1000) / 1000);
                }
            }
        }

        writeToFile(marketId + "-" + eventId + ".csv", builder.toString());
    }

    private void writeToFile(String fileName, String content)
    {
        try
        {
            File file = new File("analysis/" + fileName);

            if (!file.exists())
            {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static class DataRow
    {
        public final long timestamp;
        public final Map<String, Double> values;

        public DataRow(long timestamp, Map<String, Double> values)
        {
            this.timestamp = timestamp;
            this.values = values;
        }
    }
}