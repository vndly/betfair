package com.mauriciotogneri.betfair.models;

import com.mauriciotogneri.betfair.api.base.Enums.Side;

public class Bet
{
    public final long selectionId;
    public final Side side;
    public final double price;
    public final double stake;

    public Bet(long selectionId, Side side, double price, double stake)
    {
        this.selectionId = selectionId;
        this.side = side;
        this.price = price;
        this.stake = stake;
    }

    public double ifWin()
    {
        if (side == Side.BACK)
        {
            return (price * stake) - stake;
        }
        else
        {
            return stake;
        }
    }

    public double ifLose()
    {
        if (side == Side.BACK)
        {
            return stake;
        }
        else
        {
            return (price * stake) - stake;
        }
    }

    public String toString()
    {
        return "Selection id: " + selectionId + " - Side: " + side + " - Price: " + price + " - Stake: " + stake;
    }
}