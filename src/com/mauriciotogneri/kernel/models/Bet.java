package com.mauriciotogneri.kernel.models;

import com.mauriciotogneri.kernel.api.base.Enums.Side;

public class Bet
{
    public final long timestamp;
    public final long selectionId;
    public final Side side;
    public final double price;
    public final double stake;

    public Bet(long timestamp, long selectionId, Side side, double price, double stake)
    {
        this.timestamp = timestamp;
        this.selectionId = selectionId;
        this.side = side;
        this.price = price;
        this.stake = stake;
    }

    public String toString()
    {
        return "Timestamp: " + timestamp + " - Selection id: " + selectionId + " - Side: " + side + " - Price: " + price + " - Stake: " + stake;
    }
}