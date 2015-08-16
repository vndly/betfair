package com.mauriciotogneri.betfair.models;

import com.mauriciotogneri.betfair.utils.NumberUtils;

public class BetSimulation
{
    private double priceBack = 0;
    private double stakeBack = 0;
    private long timestampBack = 0;
    private int backBetFailed = 0;

    private double priceLay = 0;
    private double stakeLay = 0;
    private long timestampLay = 0;
    private int layBetFailed = 0;

    private double lowPriceSum = 0;
    private int lowPriceCount = 0;

    private int consecutiveValidBacks = 0;
    private int budgetRequestsFailed = 0;

    public double placeBackBet(double price, double stake, long timestamp)
    {
        priceBack = price;
        stakeBack = stake;
        timestampBack = timestamp;

        return stake;
    }

    public void backBetFailed()
    {
        backBetFailed++;
    }

    public double placeLayBet(double price, long timestamp)
    {
        priceLay = price;
        stakeLay = NumberUtils.round((stakeBack * priceBack) / priceLay, 2);
        timestampLay = timestamp;

        return (priceLay * stakeLay) * stakeLay; // liability
    }

    public void layBetFailed()
    {
        layBetFailed++;
    }

    public double getPriceBack()
    {
        return priceBack;
    }

    public double getStakeBack()
    {
        return stakeBack;
    }

    public long getTimestampBack()
    {
        return timestampBack;
    }

    public int getBackBetFailed()
    {
        return backBetFailed;
    }

    public double getPriceLay()
    {
        return priceLay;
    }

    public double getStakeLay()
    {
        return stakeLay;
    }

    public long getTimestampLay()
    {
        return timestampLay;
    }

    public int getLayBetFailed()
    {
        return layBetFailed;
    }

    public int getBudgetRequestsFailed()
    {
        return budgetRequestsFailed;
    }

    public boolean addConsecutiveValidBack(int minConsecutiveValidBacks)
    {
        consecutiveValidBacks++;

        return consecutiveValidBacks >= minConsecutiveValidBacks;
    }

    public void resetConsecutiveValidBack()
    {
        consecutiveValidBacks = 0;
    }

    public void failBudgetRequest()
    {
        budgetRequestsFailed++;
    }

    public boolean failedBudgetRequestValid(int maxBudgetRequestFails)
    {
        return budgetRequestsFailed < maxBudgetRequestFails;
    }

    public void saveLowPrice(double value)
    {
        lowPriceSum += value;
        lowPriceCount++;
    }

    public double getLowPriceAverage()
    {
        return (lowPriceCount > 0) ? NumberUtils.round(lowPriceSum / lowPriceCount, 2) : 0;
    }

    public int getLowPriceCount()
    {
        return lowPriceCount;
    }

    public boolean isBacked()
    {
        return (priceBack != 0);
    }

    public boolean isLaid()
    {
        return (priceLay != 0);
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