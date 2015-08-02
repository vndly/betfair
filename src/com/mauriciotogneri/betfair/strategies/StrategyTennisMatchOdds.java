package com.mauriciotogneri.betfair.strategies;

import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.logs.ProfitLog;
import com.mauriciotogneri.betfair.models.Selection;
import com.mauriciotogneri.betfair.models.Tick;
import com.mauriciotogneri.betfair.utils.NumberUtils;
import com.mauriciotogneri.betfair.utils.TimeUtils;

import java.io.IOException;
import java.util.List;

public class StrategyTennisMatchOdds extends Strategy
{
    private final String eventId;
    private final String marketId;
    private final Session session;

    private final CsvFile logPrice;
    private final CsvFile logActionsPlayerA;
    private final CsvFile logActionsPlayerB;

    private final BetSimulation betSimulationPlayerA = new BetSimulation();
    private final BetSimulation betSimulationPlayerB = new BetSimulation();

    private static final double MIN_BACK_PRICE = 1.1;
    private static final double MAX_PRICE_DIFF = 1.1;
    private static final double DEFAULT_STAKE = 2;
    private static final int ONE_HOUR_BEFORE_START = -(1000 * 60 * 60); // minus one hour (-01:00:00)

    private enum Player
    {
        PLAYER_A, //
        PLAYER_B
    }

    public StrategyTennisMatchOdds(Session session, String eventId, String marketId, List<Long> selections, String logFolderPath) throws IOException
    {
        this.session = session;
        this.eventId = eventId;
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

    // don't back if price is 34.00 and lay is 110.00

    @Override
    public void process(Tick tick) throws Exception
    {
        if (tick.timestamp > ONE_HOUR_BEFORE_START)
        {
            if (tick.timestamp > 0)
            {
                Selection selectionPlayerA = tick.selections.get(0);
                processSelection(Player.PLAYER_A, selectionPlayerA, betSimulationPlayerA, tick.timestamp);

                Selection selectionPlayerB = tick.selections.get(1);
                processSelection(Player.PLAYER_B, selectionPlayerB, betSimulationPlayerB, tick.timestamp);
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

    private void processSelection(Player player, Selection selection, BetSimulation betSimulation, long timestamp) throws IOException
    {
        if (betSimulation.priceBack == 0)
        {
            if (validBack(selection.back, selection.lay))
            {
                betSimulation.placeBackBet(selection.back, timestamp);

                logAction(player, timestamp, "BACKED AT: " + betSimulation.priceBack);
            }
        }
        else if (validLay(betSimulation.priceBack, selection.lay))
        {
            betSimulation.placeLayBet(selection.lay, timestamp);

            logAction(player, timestamp, "LAID AT:   " + selection.lay);
        }
    }

    // TODO: add restriction of time? => e.g. don't back after 1 hour of play
    // TODO: wait for 3 consecutive readings before to back?
    // TODO: add maximum price value? => e.g. don't back if the price is bigger than 10
    private boolean validBack(double priceBack, double priceLay)
    {
        return (priceBack >= MIN_BACK_PRICE) && ((priceLay / priceBack) <= MAX_PRICE_DIFF);
    }

    private boolean validLay(double priceBack, double priceLay)
    {
        return (priceLay < priceBack) && (priceLay > 0);
    }

    private void logAction(Player player, long timestamp, String text) throws IOException
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

    private double calculateProfit(BetSimulation betSimulation)
    {
        if (betSimulation.priceBack != 0)
        {
            if (betSimulation.priceLay != 0)
            {
                double ifWin = (betSimulation.stakeBack * betSimulation.priceBack) - betSimulation.stakeBack;
                double ifLose = (betSimulation.stakeLay * betSimulation.priceLay) - betSimulation.stakeLay;

                return NumberUtils.round(ifWin - ifLose, 2);
            }
            else
            {
                return -betSimulation.stakeBack; // we assume that we lose the bet
            }
        }

        return 0;
    }

    private void logProfit(long timestamp, double profit, BetSimulation betSimulation) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.append(TimeUtils.getTimestamp());
        csvLine.appendTimestamp(timestamp);
        csvLine.append(profit);
        csvLine.append(eventId);
        csvLine.append(marketId);

        csvLine.appendTimestamp(betSimulation.timestampBack);
        csvLine.append(betSimulation.priceBack);
        csvLine.append(betSimulation.stakeBack);

        csvLine.appendTimestamp(betSimulation.timestampLay);
        csvLine.append(betSimulation.priceLay);
        csvLine.append(betSimulation.stakeLay);

        ProfitLog.log(csvLine.toString());
    }

    @Override
    public void onClose(long timestamp) throws Exception
    {
        double profitPlayerA = calculateProfit(betSimulationPlayerA);
        logProfit(timestamp, profitPlayerA, betSimulationPlayerA);

        double profitPlayerB = calculateProfit(betSimulationPlayerB);
        logProfit(timestamp, profitPlayerB, betSimulationPlayerB);
    }

    private static class BetSimulation
    {
        public double priceBack = 0;
        public double stakeBack = 0;
        public long timestampBack = 0;

        public double priceLay = 0;
        public double stakeLay = 0;
        public long timestampLay = 0;

        public void placeBackBet(double price, long timestamp)
        {
            priceBack = price;
            stakeBack = DEFAULT_STAKE;
            timestampBack = timestamp;
        }

        public void placeLayBet(double price, long timestamp)
        {
            priceLay = price;
            stakeLay = NumberUtils.round((stakeBack * priceBack) / priceLay, 2);
            timestampLay = timestamp;
        }
    }
}