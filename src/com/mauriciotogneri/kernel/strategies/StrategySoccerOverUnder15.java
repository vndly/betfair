package com.mauriciotogneri.kernel.strategies;

import com.mauriciotogneri.kernel.Constants;
import com.mauriciotogneri.kernel.Constants.Log;
import com.mauriciotogneri.kernel.api.base.Enums.Side;
import com.mauriciotogneri.kernel.csv.CsvFile;
import com.mauriciotogneri.kernel.csv.CsvLine;
import com.mauriciotogneri.kernel.models.Bet;
import com.mauriciotogneri.kernel.models.Selection;
import com.mauriciotogneri.kernel.models.Tick;
import com.mauriciotogneri.kernel.utils.NumberUtils;

import java.io.IOException;
import java.util.List;

public class StrategySoccerOverUnder15 extends Strategy
{
    private State state = State.STARTED;

    private Bet bet = null;

    private CsvFile logPrice;
    private CsvFile logActions;

    private static final int RETREAT_LIMIT = 1000 * 60 * 60; // beginning of second half (01:00:00)
    private static final int MATCH_ALREADY_STARTED = 1000 * 60 * 10; // 10 minutes

    private static final int DEFAULT_STAKE = 2;

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
    public boolean isValid(long timestamp)
    {
        return true; //TODO: timestamp < MATCH_ALREADY_STARTED;
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
                    Selection selectionBacked = tick.getLowestBack();

                    bet = new Bet(tick.timestamp, selectionBacked.id, Side.BACK, selectionBacked.back, DEFAULT_STAKE);

                    logBet(bet);

                    state = State.BACKED;
                }
                break;

            case BACKED:
                if (isSecondHalf(tick))
                {
                    double layPrice = tick.getLayPrice(bet.selectionId);

                    if ((layPrice != 0) && (layPrice < bet.price))
                    {
                        double counterStake = NumberUtils.round((DEFAULT_STAKE * bet.price) / layPrice, 2);

                        Bet counterBet = new Bet(tick.timestamp, bet.selectionId, Side.LAY, layPrice, counterStake);

                        logBet(counterBet);

                        state = State.FINISHED;
                    }
                }

                break;
        }
    }

    private boolean isSecondHalf(Tick tick)
    {
        return tick.timestamp > RETREAT_LIMIT;
    }

    private void logBet(Bet bet) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(bet.timestamp);
        csvLine.append("BET PLACED: " + bet.toString());
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