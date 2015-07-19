package com.mauriciotogneri.kernel.api.processors;

import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.api.base.Types.Event;
import com.mauriciotogneri.kernel.api.base.Types.MarketBook;
import com.mauriciotogneri.kernel.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.kernel.api.base.Types.Runner;
import com.mauriciotogneri.kernel.api.betting.ListMarketBook;
import com.mauriciotogneri.kernel.utils.TimeFormatter;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MarketAnalyzer extends Thread
{
    private final HttpClient httpClient;
    private final Session session;

    private final Event event;
    private final String marketId;
    private final String marketType;

    private static final String SEPARATOR = ";";
    private static final int TIME = 1000;
    private static final int LOOP_LIMIT = 5 * 60;

    private volatile boolean finished = false;

    public MarketAnalyzer(HttpClient httpClient, Session session, Event event, MarketCatalogue marketCatalogue)
    {
        this.httpClient = httpClient;
        this.session = session;

        this.event = event;
        this.marketId = marketCatalogue.marketId;
        this.marketType = marketCatalogue.description.marketType;
    }

    @Override
    public void run()
    {
        try
        {
            long eventStartTime = TimeFormatter.dateToMilliseconds(event.openDate, "UTC");

            Set<String> selections = new TreeSet<>();
            List<DataRow> rows = new ArrayList<>();

            int index = 0;

            ListMarketBook listMarketBook = null;

            try
            {
                listMarketBook = ListMarketBook.getRequest(httpClient, session, marketId);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (listMarketBook != null)
            {
                while (!finished)
                {
                    try
                    {
                        long currentTime = (System.currentTimeMillis() - eventStartTime);

                        //MarketBook marketBook = ListMarketBook.get(httpClient, session, marketId);

                        ListMarketBook.Response marketBookResponse = listMarketBook.execute();

                        if (!marketBookResponse.isEmpty())
                        {
                            MarketBook marketBook = marketBookResponse.get(0);

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

                                    String minSelectionId = runner.selectionId + "-min";
                                    values.put(minSelectionId, minimumPrice);
                                    selections.add(minSelectionId);

                                    String maxSelectionId = runner.selectionId + "-max";
                                    values.put(maxSelectionId, maximumPrice);
                                    selections.add(maxSelectionId);

                                    String avgSelectionId = runner.selectionId + "-avg";
                                    values.put(avgSelectionId, averagePrice);
                                    selections.add(avgSelectionId);
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
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

    private void printResult(Set<String> selections, List<DataRow> rows)
    {
        StringBuilder builder = new StringBuilder();

        for (String selection : selections)
        {
            builder.append(SEPARATOR).append(selection);
        }

        PeriodFormatter periodFormatter = TimeFormatter.getPeriodFormatter();

        for (DataRow dataRow : rows)
        {
            builder.append("\n");

            Period period = new Period(dataRow.timestamp);
            builder.append(periodFormatter.print(period));

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

        writeToFile(event.id + "-" + marketType + "-" + marketId + ".csv", builder.toString());
    }

    private void writeToFile(String fileName, String content)
    {
        try
        {
            File file = new File("analysis/" + fileName);

            if (file.exists() || file.createNewFile())
            {
                FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(content);
                bufferedWriter.close();
            }
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