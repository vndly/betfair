package com.mauriciotogneri.betfair.strategies.kernel;

import com.mauriciotogneri.betfair.Constants.BetRules;
import com.mauriciotogneri.betfair.models.BetSimulation;
import com.mauriciotogneri.betfair.models.Selection;
import com.mauriciotogneri.betfair.models.Tick;
import com.mauriciotogneri.betfair.strategies.Strategy;

import java.io.IOException;

public abstract class StrategyTennisMatchOdds extends Strategy
{
    private final BetSimulation betSimulationPlayerA = new BetSimulation();
    private final BetSimulation betSimulationPlayerB = new BetSimulation();

    @Override
    public boolean process(Tick tick) throws Exception
    {
        if (isMoreThan(tick.timestamp, BetRules.MIN_HOUR_TO_PROCESS))
        {
            Selection selectionPlayerA = tick.selections.get(0);
            processSelection(Player.PLAYER_A, selectionPlayerA, betSimulationPlayerA, tick.timestamp);

            Selection selectionPlayerB = tick.selections.get(1);
            processSelection(Player.PLAYER_B, selectionPlayerB, betSimulationPlayerB, tick.timestamp);

            onTick(tick);
        }

        return (betSimulationPlayerA.isBacked() || betSimulationPlayerB.isBacked() || isLessThan(tick.timestamp, BetRules.MAX_HOUR_TO_PROCESS));
    }

    private void processSelection(Player player, Selection selection, BetSimulation betSimulation, long timestamp) throws IOException
    {
        if (!betSimulation.isBacked())
        {
            if (validBack(selection.back, selection.lay, timestamp))
            {
                if (betSimulation.addConsecutiveValidBack(BetRules.MIN_CONSECUTIVE_VALID_BACKS) && betSimulation.failedBudgetRequestValid(BetRules.MAX_BUDGET_REQUEST_FAILS))
                {
                    if (onRequestBudget(player, selection.back * BetRules.DEFAULT_STAKE))
                    {
                        boolean betPlaced = onPlaceBackBet(player, selection.back, BetRules.DEFAULT_STAKE, timestamp);

                        if (betPlaced)
                        {
                            double stake = betSimulation.placeBackBet(selection.back, BetRules.DEFAULT_STAKE, timestamp);
                            onUseBudget(player, stake);
                        }
                        else
                        {
                            betSimulation.backBetFailed();
                            onReturnBudget(player);
                        }
                    }
                    else
                    {
                        betSimulation.failBudgetRequest();
                    }
                }
            }
            else
            {
                betSimulation.resetConsecutiveValidBack();
            }
        }
        else if (validLay(betSimulation.getPriceBack(), selection.lay, timestamp))
        {
            if (!betSimulation.isLaid())
            {
                boolean betPlaced = onPlaceLayBet(player, selection.lay, timestamp);

                if (betPlaced)
                {
                    double liability = betSimulation.placeLayBet(selection.lay, timestamp);
                    onUseBudget(player, liability);
                }
                else
                {
                    betSimulation.layBetFailed();
                }
            }
            else
            {
                onLayPriceLow(player, timestamp, selection.lay);
            }

            betSimulation.saveLowPrice(selection.lay);
        }
    }

    private boolean validBack(double priceBack, double priceLay, long timestamp)
    {
        boolean minPriceValueValid = priceBack >= BetRules.MIN_BACK_PRICE;
        boolean maxPriceValueValid = priceBack <= BetRules.MAX_BACK_PRICE;
        boolean maxPriceDiffValid = (priceLay / priceBack) <= BetRules.MAX_PRICE_DIFF;
        boolean minTimeLimitValid = isMoreThan(timestamp, BetRules.MIN_HOUR_TO_BACK);
        boolean maxTimeLimitValid = isLessThan(timestamp, BetRules.MAX_HOUR_TO_BACK);

        return (minPriceValueValid && maxPriceValueValid && maxPriceDiffValid && minTimeLimitValid && maxTimeLimitValid);
    }

    private boolean validLay(double priceBack, double priceLay, long timestamp)
    {
        boolean priceLowerThanBack = priceLay < priceBack;
        boolean priceBiggerThanZero = priceLay > 0;
        boolean maxTimeIdealValid = isMoreThan(timestamp, BetRules.MAX_TIME_IDEAL_LAY);
        boolean idealPriceValid = priceLay <= (priceBack * BetRules.IDEAL_PRICE_FACTOR);

        return (priceLowerThanBack && priceBiggerThanZero && (maxTimeIdealValid || idealPriceValid));
    }

    private boolean isMoreThan(long timestamp, double hours)
    {
        return timestamp >= (1000 * 60 * (60 * hours));
    }

    private boolean isLessThan(long timestamp, double hours)
    {
        return timestamp <= (1000 * 60 * (60 * hours));
    }

    @Override
    public void onClose(long timestamp, boolean executed) throws Exception
    {
        onClose(Player.PLAYER_A, executed, timestamp, betSimulationPlayerA);
        onClose(Player.PLAYER_B, executed, timestamp, betSimulationPlayerB);
    }

    protected abstract void onTick(Tick tick) throws IOException;

    protected abstract boolean onRequestBudget(Player player, double value) throws IOException;

    protected abstract void onUseBudget(Player player, double value) throws IOException;

    protected abstract void onReturnBudget(Player player) throws IOException;

    protected abstract boolean onPlaceBackBet(Player player, double price, double stake, long timestamp) throws IOException;

    protected abstract boolean onPlaceLayBet(Player player, double price, long timestamp) throws IOException;

    protected abstract void onLayPriceLow(Player player, long timestamp, double value) throws IOException;

    protected abstract void onClose(Player player, boolean executed, long timestamp, BetSimulation betSimulation) throws IOException;
}