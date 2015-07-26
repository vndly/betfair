package com.mauriciotogneri.kernel.strategies;

import com.mauriciotogneri.kernel.api.base.Enums.EventTypeEnum;
import com.mauriciotogneri.kernel.api.base.Enums.MarketTypeEnum;
import com.mauriciotogneri.kernel.models.Tick;
import com.mauriciotogneri.kernel.utils.StringUtils;

import java.io.IOException;
import java.util.List;

public abstract class Strategy
{
    public abstract boolean isValid(long timestamp);

    public abstract void process(Tick tick) throws Exception;

    public static Strategy getStrategy(String eventType, String marketType, String marketId, List<Long> selections, String folderPath) throws IOException
    {
        if (StringUtils.equals(eventType, EventTypeEnum.SOCCER.toString()))
        {
            if (StringUtils.equals(marketType, MarketTypeEnum.OVER_UNDER_15.toString()))
            {
                return new StrategySoccerOverUnder15(selections, folderPath);
            }
        }

        return null;
    }
}