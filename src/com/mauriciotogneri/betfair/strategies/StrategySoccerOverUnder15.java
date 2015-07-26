package com.mauriciotogneri.betfair.strategies;

import com.mauriciotogneri.betfair.Constants;
import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.api.base.Enums.Side;
import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.models.Bet;
import com.mauriciotogneri.betfair.models.Selection;
import com.mauriciotogneri.betfair.models.Tick;
import com.mauriciotogneri.betfair.utils.NumberUtils;

import java.io.IOException;
import java.util.List;

public class StrategySoccerOverUnder15 extends Strategy
{
    private State state = State.STARTED;

    private Bet initialBet = null;
    private Bet counterBet = null;

    private CsvFile logPrice;
    private CsvFile logActions;

    private static final int END_FIRST_HALF = 1000 * 60 * 45; // end of first half (00:45:00)
    private static final int END_HALF_TIME = 1000 * 60 * 60; // end of first half (01:00:00)
    private static final int END_SECOND_HALF = 1000 * 60 * 110; // end of second half (01:50:00)

    private static final double DEFAULT_STAKE = 2;
    private static final double MIN_BACK_PRICE = 1.1;
    private static final double MIN_LAY_PRICE = 1.02;

    private enum State
    {
        STARTED, BACKED, FINISHED
    }

    public StrategySoccerOverUnder15(List<Long> selections, String logFolderPath) throws IOException
    {
        logPrice = new CsvFile(logFolderPath + Log.PRICES_LOG_FILE);

        CsvLine csvLine = new CsvLine();
        csvLine.separator();

        for (long selectionId : selections)
        {
            csvLine.append(selectionId + "-back");
            csvLine.append(selectionId + "-lay");
        }

        logPrice.write(csvLine);

        logActions = new CsvFile(logFolderPath + Constants.Log.ACTIONS_LOG_FILE);
    }

    @Override
    public void onClose(long timestamp) throws Exception
    {
        if (initialBet != null)
        {
            if (counterBet != null)
            {
                double ifBack = initialBet.ifWin() - counterBet.ifLose();
                double ifLay = counterBet.ifWin() - initialBet.ifLose();

                logProfit(timestamp, (ifBack + ifLay) / 2);
            }
            else
            {
                if (isMatchFinished(timestamp))
                {
                    logProfit(timestamp, -initialBet.ifLose());
                }
                else
                {
                    logProfit(timestamp, initialBet.ifWin());
                }
            }
        }
    }

    @Override
    public void process(Tick tick) throws Exception
    {
        processAction(tick);
        logPrice(tick);
    }

    private void processAction(Tick tick) throws IOException
    {
        switch (state)
        {
            case STARTED:

                if (tick.allBackAvailable())
                {
                    Selection selection = tick.getLowestBack();

                    if ((selection.back >= MIN_BACK_PRICE) && (!isFirstHalfFinished(tick.timestamp)))
                    {
                        initialBet = new Bet(selection.id, Side.BACK, selection.back, DEFAULT_STAKE, selection.index);

                        logBet(tick.timestamp, initialBet);

                        state = State.BACKED;
                    }
                }
                break;

            case BACKED:

                double layPrice = tick.getLayPrice(initialBet.selectionId);

                if (isFirstHalfFinished(tick.timestamp) || (layPrice < MIN_LAY_PRICE))
                {
                    if ((layPrice != 0) && (layPrice < initialBet.price))
                    {
                        double counterStake = NumberUtils.round((DEFAULT_STAKE * initialBet.price) / layPrice, 2);

                        counterBet = new Bet(initialBet.selectionId, Side.LAY, layPrice, counterStake, initialBet.selectionIndex);

                        logBet(tick.timestamp, counterBet);

                        state = State.FINISHED;
                    }
                }

                break;
        }
    }

    private boolean isFirstHalfFinished(long timestamp)
    {
        return timestamp > END_FIRST_HALF;
    }

    private boolean isMatchFinished(long timestamp)
    {
        return timestamp > END_SECOND_HALF;
    }

    private void logBet(long timestamp, Bet bet) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(timestamp);
        csvLine.append("BET PLACED: " + bet.toString());
        logActions.write(csvLine);
    }

    private void logProfit(long timestamp, double profit) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(timestamp);
        csvLine.append("PROFIT: " + profit);
        logActions.write(csvLine);
    }

    private void logPrice(Tick tick) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(tick.timestamp);

        for (Selection selection : tick.selections)
        {
            csvLine.append(selection.back);
            csvLine.append(selection.lay);
        }

        logPrice.write(csvLine);
    }
}