package com.mauriciotogneri.betfair.models;

import com.mauriciotogneri.betfair.api.base.Enums.Side;

public class Bet extends BetInstruction
{
    public final String id;
    public final String placedDate;
    public final double averagePriceMatched;
    public final double sizeMatched;
    public final boolean isMatched;

    public Bet(BetInstruction betInstruction, String id, String placedDate, double averagePriceMatched, double sizeMatched, boolean isMatched)
    {
        super(betInstruction);

        this.id = id;
        this.placedDate = placedDate;
        this.averagePriceMatched = averagePriceMatched;
        this.sizeMatched = sizeMatched;
        this.isMatched = isMatched;
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
        StringBuilder builder = new StringBuilder();
        builder.append("Bet id: ").append(id);
        builder.append(" - Reference: ").append(getRef());
        builder.append(" - Market id: ").append(marketId);
        builder.append(" - Selection id: ").append(selectionId);
        builder.append(" - Side: ").append(side);
        builder.append(" - Price: ").append(price);
        builder.append(" - Stake: ").append(stake);
        builder.append(" - Placed date: ").append(placedDate);
        builder.append(" - Avg. price matched: ").append(averagePriceMatched);
        builder.append(" - Size matched: ").append(sizeMatched);
        builder.append(" - Is matched: ").append(isMatched);

        return builder.toString();
    }
}