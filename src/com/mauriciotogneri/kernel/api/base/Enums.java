package com.mauriciotogneri.kernel.api.base;

@SuppressWarnings("unused")
public class Enums
{
    public enum EventTypeEnum
    {
        SOCCER("1"), //
        TENNIS("2"), //
        GOLF("3"), //
        CRICKET("4"), //
        RUGBY_UNION("5"), //
        BOXING("6"), //
        HORSE_RACING("7"), //
        MOTOR_SPORT("8"), //
        CYCLING("11"), //
        RUGBY_LEAGUE("1477"), //
        AMERICAN_FOOTBALL("6423"), //
        BASEBALL("7511"), //
        BASKETBALL("7522");

        private final String id;

        EventTypeEnum(String id)
        {
            this.id = id;
        }

        public String toString()
        {
            return id;
        }
    }

    public enum MarketTypeEnum
    {
        MATCH_ODDS, //
        OVER_UNDER_05, //
        OVER_UNDER_15, //
        OVER_UNDER_25, //
        CORRECT_SCORE
    }

    public enum MarketProjection
    {
        COMPETITION, //
        EVENT,//
        EVENT_TYPE,//
        MARKET_START_TIME, //
        MARKET_DESCRIPTION, //
        RUNNER_DESCRIPTION,//
        RUNNER_METADATA
    }

    public enum MarketSort
    {
        MINIMUM_TRADED, //
        MAXIMUM_TRADED, //
        MINIMUM_AVAILABLE, //
        MAXIMUM_AVAILABLE,//
        FIRST_TO_START, //
        LAST_TO_START
    }

    public enum MarketBettingType
    {
        ODDS, //
        LINE,//
        RANGE,//
        ASIAN_HANDICAP_DOUBLE_LINE, //
        ASIAN_HANDICAP_SINGLE_LINE,//
        FIXED_ODDS
    }

    public enum OrderProjection
    {
        ALL, //
        EXECUTABLE, //
        EXECUTION_COMPLETE
    }

    public enum MatchProjection
    {
        NO_ROLLUP, //
        ROLLED_UP_BY_PRICE, //
        ROLLED_UP_BY_AVG_PRICE
    }

    public enum PriceData
    {
        SP_AVAILABLE, //
        SP_TRADED, //
        EX_BEST_OFFERS, //
        EX_ALL_OFFERS, //
        EX_TRADED
    }

    public enum MarketStatus
    {
        INACTIVE, //
        OPEN, //
        SUSPENDED, //
        CLOSED
    }

    public enum RunnerStatus
    {
        ACTIVE, //
        WINNER, //
        LOSER, //
        REMOVED_VACANT, //
        REMOVED, //
        HIDDEN
    }

    public enum RollupModel
    {
        STAKE,//
        PAYOUT, //
        MANAGED_LIABILITY, //
        NONE
    }

    public enum Side
    {
        BACK, //
        LAY
    }
}