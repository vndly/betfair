package com.mauriciotogneri.betfair.strategies;

import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.logs.ProfitLog;
import com.mauriciotogneri.betfair.models.Selection;
import com.mauriciotogneri.betfair.models.Tick;
import com.mauriciotogneri.betfair.models.Wallet;
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

    private int consecutiveValidBacks = 0;

    private final BetSimulation betSimulationPlayerA = new BetSimulation();
    private final BetSimulation betSimulationPlayerB = new BetSimulation();

    private static final int MIN_CONSECUTIVE_VALID_BACKS = 3;

    private static final double MIN_BACK_PRICE = 1.1;
    private static final double MAX_BACK_PRICE = 4.0;

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
                consecutiveValidBacks++;

                if (consecutiveValidBacks >= MIN_CONSECUTIVE_VALID_BACKS)
                {
                    double budget = DEFAULT_STAKE + (selection.back * 2);

                    if (Wallet.getInstance().requestBudget(budget))
                    {
                        consecutiveValidBacks = 0;

                        betSimulation.placeBackBet(selection.back, timestamp);

                        logAction(player, timestamp, "BACKED AT: " + betSimulation.priceBack);
                    }
                }
            }
            else
            {
                consecutiveValidBacks = 0;
            }
        }
        else if (validLay(betSimulation.priceBack, selection.lay))
        {
            betSimulation.placeLayBet(selection.lay, timestamp);

            logAction(player, timestamp, "LAID AT:   " + selection.lay);
        }
    }

    // TODO: add restriction of time? => e.g. don't back after 1 hour of play
    private boolean validBack(double priceBack, double priceLay)
    {
        return (priceBack >= MIN_BACK_PRICE) && (priceBack <= MAX_BACK_PRICE) && ((priceLay / priceBack) <= MAX_PRICE_DIFF);
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

    private void logProfit(long timestamp, BetSimulation betSimulation) throws IOException
    {
        double profit = betSimulation.getProfit();

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

        if (profit > 0)
        {
            Wallet.getInstance().addProfit(profit + DEFAULT_STAKE);
        }
    }

    @Override
    public void onClose(long timestamp) throws Exception
    {
        logProfit(timestamp, betSimulationPlayerA);

        logProfit(timestamp, betSimulationPlayerB);
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

        public double getProfit()
        {
            if (priceBack != 0)
            {
                if (priceLay != 0)
                {
                    double ifWin = (stakeBack * priceBack) - stakeBack;
                    double ifLose = (stakeLay * priceLay) - stakeLay;

                    return NumberUtils.round(ifWin - ifLose, 2);
                }
                else
                {
                    return -stakeBack; // we assume that we lose the bet
                }
            }

            return 0;
        }
    }
}