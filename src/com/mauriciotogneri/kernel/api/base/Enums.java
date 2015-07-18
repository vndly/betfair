package com.mauriciotogneri.kernel.api.base;

public class Enums
{
    public enum EventTypeEnum
    {
        SOCCER("1"), TENNIS("2"), GOLF("3");

        private String id;

        EventTypeEnum(String id)
        {
            this.id = id;
        }

        public String toString()
        {
            return id;
        }
    }

    public enum MarketProjection
    {
        COMPETITION, EVENT, EVENT_TYPE, MARKET_START_TIME, MARKET_DESCRIPTION, RUNNER_DESCRIPTION, RUNNER_METADATA;
    }

    public enum MarketSort
    {
        MINIMUM_TRADED, MAXIMUM_TRADED, MINIMUM_AVAILABLE, MAXIMUM_AVAILABLE, FIRST_TO_START, LAST_TO_START;
    }

    public enum MarketBettingType
    {
        ODDS, LINE, RANGE, ASIAN_HANDICAP_DOUBLE_LINE, ASIAN_HANDICAP_SINGLE_LINE, FIXED_ODDS;
    }
}