package com.mauriciotogneri.betfair.models;

import com.mauriciotogneri.betfair.api.base.Enums.Side;

public class BetInstruction
{
    public final String marketId;
    public final long selectionId;
    public final Side side;
    public final double price;
    public final double stake;

    public BetInstruction(String marketId, long selectionId, Side side, double price, double stake)
    {
        this.marketId = marketId;
        this.selectionId = selectionId;
        this.side = side;
        this.price = price;
        this.stake = stake;
    }

    public BetInstruction(BetInstruction betInstruction)
    {
        this(betInstruction.marketId, betInstruction.selectionId, betInstruction.side, betInstruction.price, betInstruction.stake);
    }

    public String getRef()
    {
        return marketId + "_" + side + "_" + price + "_" + stake;
    }
}