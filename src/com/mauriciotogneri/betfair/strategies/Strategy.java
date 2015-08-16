package com.mauriciotogneri.betfair.strategies;

import com.mauriciotogneri.betfair.api.base.Enums.EventTypeEnum;
import com.mauriciotogneri.betfair.api.base.Enums.MarketTypeEnum;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.models.Tick;
import com.mauriciotogneri.betfair.strategies.custom.StrategyTennisMatchOddsCustom;

import java.io.IOException;
import java.util.List;

public abstract class Strategy
{
    public enum Player
    {
        PLAYER_A, //
        PLAYER_B
    }

    public abstract void onClose(long timestamp, boolean executed) throws Exception;

    public abstract boolean process(Tick tick) throws Exception;

    public static Strategy getStrategy(Session session, EventTypeEnum eventType, MarketTypeEnum marketType, String eventId, String marketId, List<Long> selections, String folderPath) throws IOException
    {
        switch (eventType)
        {
            case TENNIS:
                if (marketType == MarketTypeEnum.MATCH_ODDS)
                {
                    return new StrategyTennisMatchOddsCustom(session, eventId, marketId, selections, folderPath);
                }
                break;
        }

        return null;
    }
}