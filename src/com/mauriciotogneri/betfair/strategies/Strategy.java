package com.mauriciotogneri.betfair.strategies;

import com.mauriciotogneri.betfair.api.base.Enums.EventTypeEnum;
import com.mauriciotogneri.betfair.api.base.Enums.MarketTypeEnum;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.models.Tick;
import com.mauriciotogneri.betfair.utils.StringUtils;

import java.io.IOException;
import java.util.List;

public abstract class Strategy
{
    public abstract void onClose(long timestamp) throws Exception;

    public abstract void process(Tick tick) throws Exception;

    public static Strategy getStrategy(Session session, String eventType, String marketType, String marketId, List<Long> selections, String folderPath) throws IOException
    {
        if (StringUtils.equals(eventType, EventTypeEnum.SOCCER.toString()))
        {
            if (StringUtils.equals(marketType, MarketTypeEnum.OVER_UNDER_15.toString()))
            {
                return new StrategySoccerOverUnder15(session, marketId, selections, folderPath);
            }
        }

        return null;
    }
}