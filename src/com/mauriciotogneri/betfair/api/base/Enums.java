package com.mauriciotogneri.betfair.api.base;

import com.mauriciotogneri.betfair.utils.StringUtils;

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

        public static EventTypeEnum get(String code)
        {
            for (EventTypeEnum eventType : EventTypeEnum.values())
            {
                if (StringUtils.equals(code, eventType.toString()))
                {
                    return eventType;
                }
            }

            return null;
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

    public enum OrderType
    {
        LIMIT, //
        LIMIT_ON_CLOSE, //
        MARKET_ON_CLOSE
    }

    public enum PersistenceType
    {
        LAPSE, //
        PERSIST, //
        MARKET_ON_CLOSE
    }

    public enum ExecutionReportStatus
    {
        SUCCESS, //
        FAILURE, //
        PROCESSED_WITH_ERRORS, //
        TIMEOUT
    }

    public enum ExecutionReportErrorCode
    {
        ERROR_IN_MATCHER, //
        PROCESSED_WITH_ERRORS,//
        BET_ACTION_ERROR,//
        INVALID_ACCOUNT_STATE,//
        INVALID_WALLET_STATUS,//
        INSUFFICIENT_FUNDS,//
        LOSS_LIMIT_EXCEEDED,//
        MARKET_SUSPENDED,//
        MARKET_NOT_OPEN_FOR_BETTING,//
        DUPLICATE_TRANSACTION,//
        INVALID_ORDER,//
        INVALID_MARKET_ID,//
        PERMISSION_DENIED,//
        DUPLICATE_BETIDS,//
        NO_ACTION_REQUIRED,//
        SERVICE_UNAVAILABLE,//
        REJECTED_BY_REGULATOR
    }

    public enum InstructionReportStatus
    {
        SUCCESS, //
        FAILURE, //
        TIMEOUT
    }

    public enum InstructionReportErrorCode
    {
        INVALID_BET_SIZE, //
        INVALID_RUNNER, //
        BET_TAKEN_OR_LAPSED, //
        BET_IN_PROGRESS, //
        RUNNER_REMOVED, //
        MARKET_NOT_OPEN_FOR_BETTING, //
        LOSS_LIMIT_EXCEEDED, //
        MARKET_NOT_OPEN_FOR_BSP_BETTING, //
        INVALID_PRICE_EDIT, //
        INVALID_ODDS, //
        INSUFFICIENT_FUNDS, //
        INVALID_PERSISTENCE_TYPE, //
        ERROR_IN_MATCHER, //
        INVALID_BACK_LAY_COMBINATION, //
        ERROR_IN_ORDER, //
        INVALID_BID_TYPE, //
        INVALID_BET_ID, //
        CANCELLED_NOT_PLACED, //
        RELATED_ACTION_FAILED, //
        NO_ACTION_REQUIRED
    }
}