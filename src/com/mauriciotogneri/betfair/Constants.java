package com.mauriciotogneri.betfair;

public class Constants
{
    private Constants()
    {
    }

    public static class Debug
    {
        public static final boolean ENABLE_LOGS = false;

        public static class Http
        {
            public static final boolean PRINT_URL = true;
            public static final boolean PRINT_HEADERS = false;
            public static final boolean PRINT_PARAMETERS = true;
            public static final boolean PRINT_RESPONSE = true;
        }
    }

    public static class BetRules
    {
        public static final int START_HOUR_TO_PROCESS = -1;
        public static final int MAX_HOUR_TO_PROCESS = 5;

        public static final int MIN_CONSECUTIVE_VALID_BACKS = 3;
        public static final int MAX_BUDGET_REQUEST_FAILS = 10;

        public static final double MIN_BACK_PRICE = 1.1;
        public static final double MAX_BACK_PRICE = 4.0;

        public static final double MAX_PRICE_DIFF = 1.1;
        public static final double IDEAL_PRICE_FACTOR = 0.8;
        public static final double DEFAULT_STAKE = 2;
    }

    public static class Log
    {
        public static final String BASE_LOG_PATH = "logs/";
        public static final String ERROR_LOG_PATH = BASE_LOG_PATH + "error.log";
        public static final String THREAD_LOG_PATH = BASE_LOG_PATH + "thread.log";
        public static final String PROFIT_LOG_PATH = BASE_LOG_PATH + "profit.csv";
        public static final String WALLET_LOG_PATH = BASE_LOG_PATH + "wallet.csv";
        public static final String FUNDS_LOG_PATH = BASE_LOG_PATH + "funds.csv";
        public static final String ACTIVITY_LOG_PATH = BASE_LOG_PATH + "activity.log";
        public static final String EVENT_LOG_PATH = BASE_LOG_PATH + "events/";

        public static final String INFO_LOG_FILE = "info.json";
        public static final String STATUS_LOG_FILE = "status.csv";
        public static final String PRICES_LOG_FILE = "prices.csv";
        public static final String BETS_LOG_FILE = "bets.log";
        public static final String ACTIVITY_LOG_FILE = "activity.log";
        public static final String ACTIONS_LOG_FILE = "actions.csv";
    }

    public static class Email
    {
        public static final String SENDER_EMAIL = "betbot@zeronest.com";
        public static final String RECEIVER_EMAIL = "mauricio.togneri@gmail.com";
    }

    public static class Execution
    {
        public static final String IS_RUNNING_FLAG = "running.txt";
    }
}