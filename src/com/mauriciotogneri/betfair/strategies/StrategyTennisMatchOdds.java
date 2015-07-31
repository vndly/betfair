package com.mauriciotogneri.betfair.strategies;

import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.models.Selection;
import com.mauriciotogneri.betfair.models.Tick;

import java.io.IOException;
import java.util.List;

public class StrategyTennisMatchOdds extends Strategy
{
    private final String marketId;
    private final Session session;

    private final CsvFile logPrice;
    private final CsvFile logActionsPlayerA;
    private final CsvFile logActionsPlayerB;

    private double backPricePlayerA = 0;
    private double backPricePlayerB = 0;

    private static final int ONE_HOUR_BEFORE_START = -(1000 * 60 * 60); // minus one hour (-01:00:00)

    private enum Player
    {
        PLAYER_A, //
        PLAYER_B
    }

    public StrategyTennisMatchOdds(Session session, String marketId, List<Long> selections, String logFolderPath) throws IOException
    {
        this.session = session;
        this.marketId = marketId;

        this.logPrice = new CsvFile(logFolderPath + Log.PRICES_LOG_FILE);
        this.logActionsPlayerA = new CsvFile(logFolderPath + "actionsA.csv");
        this.logActionsPlayerB = new CsvFile(logFolderPath + "actionsB.csv");

        initLogPrice(logPrice, selections);
    }

    private void initLogPrice(CsvFile logPrice, List<Long> selections) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.separator();

        for (Long selectionId : selections)
        {
            csvLine.append(selectionId + "-back");
            csvLine.append(selectionId + "-lay");
        }

        logPrice.write(csvLine);
    }

    @Override
    public void process(Tick tick) throws Exception
    {
        if (tick.timestamp > ONE_HOUR_BEFORE_START)
        {
            if (tick.timestamp > 0)
            {
                Selection selectionPlayerA = tick.selections.get(0);
                Selection selectionPlayerB = tick.selections.get(1);

                if (backPricePlayerA == 0)
                {
                    if (selectionPlayerA.back > 0)
                    {
                        backPricePlayerA = selectionPlayerA.back;

                        addAction(Player.PLAYER_A, tick.timestamp, "BACKED AT: " + backPricePlayerA);
                    }
                }
                else if (selectionPlayerA.lay < backPricePlayerA)
                {
                    addAction(Player.PLAYER_A, tick.timestamp, "LAID AT:   " + selectionPlayerA.lay);
                }

                if (backPricePlayerB == 0)
                {
                    if (selectionPlayerB.back > 0)
                    {
                        backPricePlayerB = selectionPlayerB.back;

                        addAction(Player.PLAYER_B, tick.timestamp, "BACKED AT: " + backPricePlayerB);
                    }
                }
                else if (selectionPlayerB.lay < backPricePlayerB)
                {
                    addAction(Player.PLAYER_B, tick.timestamp, "LAID AT:   " + selectionPlayerB.lay);
                }
            }

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

    private void addAction(Player player, long timestamp, String text) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(timestamp);
        csvLine.append(text);

        switch (player)
        {
            case PLAYER_A:
                logActionsPlayerA.write(csvLine);
                break;

            case PLAYER_B:
                logActionsPlayerB.write(csvLine);
                break;
        }
    }

    @Override
    public void onClose(long timestamp) throws Exception
    {
    }
}