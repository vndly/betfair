package com.mauriciotogneri.betfair.tools;

import com.mauriciotogneri.betfair.models.BetSimulation;
import com.mauriciotogneri.betfair.models.Selection;
import com.mauriciotogneri.betfair.models.Tick;
import com.mauriciotogneri.betfair.utils.IoUtils;
import com.mauriciotogneri.betfair.utils.NumberUtils;
import com.mauriciotogneri.betfair.utils.StringUtils;
import com.mauriciotogneri.betfair.utils.TimeUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class RunSimulation
{
    public static void main(String[] args)
    {
        RunSimulation runSimulation = new RunSimulation();
        runSimulation.execute("logs/events/2");
    }

    private double execute(String rootPath)
    {
        List<File> fileList = new ArrayList<>();

        File root = new File(rootPath);
        loadFiles(root, fileList);

        return processFiles(fileList, rootPath);
    }

    private void loadFiles(File folder, List<File> result)
    {
        File[] files = folder.listFiles();

        if (files != null)
        {
            for (File file : files)
            {
                if (file.isDirectory())
                {
                    loadFiles(file, result);
                }
                else if (file.isFile() && file.getName().endsWith(".csv"))
                {
                    result.add(file);
                }
            }
        }
    }

    private double displayResults(List<BetSimulation> betSimulationList, String rootPath)
    {
        double positiveProfit = 0;
        double negativeProfit = 0;
        int countPositiveProfit = 0;
        int countNegativeProfit = 0;
        int countZeroProfit = 0;

        try
        {
            File file = new File(rootPath, "result.html");

            FileChannel outChan = new FileOutputStream(file, true).getChannel();
            outChan.truncate(0);
            outChan.close();

            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

            bufferWriter.write(getDocumentHeader());

            StringBuilder rows = new StringBuilder();

            if (file.exists() || file.createNewFile())
            {
                for (int i = 0; i < betSimulationList.size(); i++)
                {
                    BetSimulation betSimulation = betSimulationList.get(i);

                    double profit = betSimulation.getProfit();

                    if (profit > 0)
                    {
                        positiveProfit += profit;
                        countPositiveProfit++;
                    }
                    else if (profit < 0)
                    {
                        negativeProfit += profit;
                        countNegativeProfit++;
                    }
                    else
                    {
                        countZeroProfit++;
                    }

                    String row = getSimulationRow(betSimulation, i);

                    if (!StringUtils.isEmpty(row))
                    {
                        rows.append(row);
                    }
                }
            }

            String totalProfitText = "TOTAL PROFIT:     " + NumberUtils.round(positiveProfit + negativeProfit, 2);
            bufferWriter.write(totalProfitText + "<br/>");
            System.out.println(totalProfitText);

            String positiveProfitText = "POSITIVE PROFIT:  " + NumberUtils.round(positiveProfit, 2);
            bufferWriter.write(positiveProfitText + "<br/>");
            System.out.println(positiveProfitText);

            String negativeProfitText = "NEGATIVE PROFIT:  " + NumberUtils.round(negativeProfit, 2);
            bufferWriter.write(negativeProfitText + "<br/><br/>");
            System.out.println(negativeProfitText + "\n");

            String countPositiveProfitText = "COUNT POSITIVE PROFIT:  " + countPositiveProfit;
            bufferWriter.write(countPositiveProfitText + "<br/>");
            System.out.println(countPositiveProfitText);

            String countNegativeProfitText = "COUNT NEGATIVE PROFIT:  " + countNegativeProfit;
            bufferWriter.write(countNegativeProfitText + "<br/>");
            System.out.println(countNegativeProfitText);

            String countZeroProfitText = "COUNT ZERO PROFIT:      " + countZeroProfit;
            bufferWriter.write(countZeroProfitText + "<br/><br/>");
            System.out.println(countZeroProfitText);

            bufferWriter.write(getTableHeader());
            bufferWriter.write(rows.toString());
            bufferWriter.write(getDocumentFooter());

            bufferWriter.flush();
            bufferWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return NumberUtils.round(positiveProfit + negativeProfit, 2);
    }

    private String getDocumentHeader()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("<html>");

        builder.append("<head>");
        builder.append("<title>Simulator</title>");
        builder.append("<style>");

        builder.append("table").append("\n");
        builder.append("{").append("\n");
        builder.append("font-family:'Courier';").append("\n");
        builder.append("}").append("\n");

        builder.append("a").append("\n");
        builder.append("{").append("\n");
        builder.append("text-decoration:none;").append("\n");
        builder.append("}").append("\n");

        builder.append(".header").append("\n");
        builder.append("{").append("\n");
        builder.append("font-weight:bold;").append("\n");
        builder.append("text-align:center;").append("\n");
        builder.append("background-color:#DDDDDD;").append("\n");
        builder.append("}").append("\n");

        builder.append(".cell").append("\n");
        builder.append("{").append("\n");
        builder.append("text-align:center;").append("\n");
        builder.append("}").append("\n");

        builder.append("</style>");
        builder.append("</head>");

        builder.append("<body style='font-family:Courier'>");


        return builder.toString();
    }

    private String getTableHeader()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("<table border=\"1\" cellpadding=\"8\">");
        builder.append("<tr>");

        builder.append("<td class='header'>PROFIT</td>");

        builder.append("<td class='header'>TIME BACK</td>");
        builder.append("<td class='header'>PRICE BACK</td>");
        builder.append("<td class='header'>STAKE BACK</td>");

        builder.append("<td class='header'>TIME LAY</td>");
        builder.append("<td class='header'>PRICE LAY</td>");
        builder.append("<td class='header'>STAKE LAY</td>");

        return builder.toString();
    }

    private String getDocumentFooter()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("</tr>");
        builder.append("</table>");
        builder.append("</body>");
        builder.append("</html>");

        return builder.toString();
    }

    private String getSimulationRow(BetSimulation betSimulation, int index)
    {
        StringBuilder builder = new StringBuilder();

        double profit = betSimulation.getProfit();

        if (profit != 0)
        {
            String color = ((index % 2) == 0) ? "#FFFFFF" : "#EEEEEE";

            builder.append("<tr>");

            builder.append("<td class='cell' style='background-color:").append(color).append("'>").append(profit).append("</td>");

            builder.append("<td class='cell' style='background-color:").append(color).append("'>").append(TimeUtils.getPeriod(betSimulation.getTimestampBack())).append("</td>");
            builder.append("<td class='cell' style='background-color:").append(color).append("'>").append(betSimulation.getPriceBack()).append("</td>");
            builder.append("<td class='cell' style='background-color:").append(color).append("'>").append(betSimulation.getStakeBack()).append("</td>");

            builder.append("<td class='cell' style='background-color:").append(color).append("'>").append(TimeUtils.getPeriod(betSimulation.getTimestampLay())).append("</td>");
            builder.append("<td class='cell' style='background-color:").append(color).append("'>").append(betSimulation.getPriceLay()).append("</td>");
            builder.append("<td class='cell' style='background-color:").append(color).append("'>").append(betSimulation.getStakeLay()).append("</td>");

            builder.append("</tr>");
        }

        return builder.toString();
    }

    private double processFiles(List<File> fileList, String rootPath)
    {
        List<BetSimulation> betSimulationList = new ArrayList<>();

        for (File file : fileList)
        {
            BetSimulation[] result = processFile(file);

            if (result != null)
            {
                betSimulationList.add(result[0]);
                betSimulationList.add(result[1]);
            }
        }

        return displayResults(betSimulationList, rootPath);
    }

    private BetSimulation[] processFile(File file)
    {
        StrategyTennisMatchOddsSimulation strategy = new StrategyTennisMatchOddsSimulation();
        BufferedReader bufferedReader = null;

        try
        {
            bufferedReader = new BufferedReader(new FileReader(file));
            bufferedReader.readLine();

            String line;
            long lastTimestamp = 0;

            while ((line = bufferedReader.readLine()) != null)
            {
                Tick tick = getTick(line);
                strategy.process(tick);

                lastTimestamp = tick.timestamp;
            }

            strategy.onClose(lastTimestamp, true);

            BetSimulation[] result = new BetSimulation[2];
            result[0] = strategy.getBetSimulationPlayerA();
            result[1] = strategy.getBetSimulationPlayerB();

            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            IoUtils.closeResource(bufferedReader);
        }

        return null;
    }

    private Tick getTick(String line)
    {
        String[] parts = line.split(",");

        long timestamp = TimeUtils.fromPeriod(parts[0]);
        double backPlayerA = Double.parseDouble(parts[1]);
        double layPlayerA = Double.parseDouble(parts[2]);
        double backPlayerB = Double.parseDouble(parts[3]);
        double layPlayerB = Double.parseDouble(parts[4]);

        Tick tick = new Tick(timestamp);
        tick.add(new Selection(backPlayerA, layPlayerA));
        tick.add(new Selection(backPlayerB, layPlayerB));

        return tick;
    }
}