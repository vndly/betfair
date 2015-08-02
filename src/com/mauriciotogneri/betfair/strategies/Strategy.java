package com.mauriciotogneri.betfair.strategies;

import com.mauriciotogneri.betfair.api.base.Enums.EventTypeEnum;
import com.mauriciotogneri.betfair.api.base.Enums.MarketTypeEnum;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.models.Tick;

import java.io.IOException;
import java.util.List;

public abstract class Strategy
{
    public abstract void onClose(long timestamp) throws Exception;

    public abstract void process(Tick tick) throws Exception;

    public static Strategy getStrategy(Session session, EventTypeEnum eventType, MarketTypeEnum marketType, String eventId, String marketId, List<Long> selections, String folderPath) throws IOException
    {
        switch (eventType)
        {
            case SOCCER:
                if (marketType == MarketTypeEnum.OVER_UNDER_05)
                {
                    return new StrategySoccerOverUnder05(session, marketId, selections, folderPath);
                }
                else if (marketType == MarketTypeEnum.OVER_UNDER_15)
                {
                    return new StrategySoccerOverUnder15(session, marketId, selections, folderPath);
                }
                break;

            case TENNIS:
                if (marketType == MarketTypeEnum.MATCH_ODDS)
                {
                    return new StrategyTennisMatchOdds(session, eventId, marketId, selections, folderPath);
                }
                break;
        }

        return null;
    }
}