package com.mauriciotogneri.betfair;

public class Constants
{
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

    public static class Log
    {
        public static final String BASE_LOG_PATH = "logs/";
        public static final String ERROR_LOG_PATH = BASE_LOG_PATH + "error.log";
        public static final String PROFIT_LOG_PATH = BASE_LOG_PATH + "profit.csv";
        public static final String WALLET_LOG_PATH = BASE_LOG_PATH + "wallet.csv";
        public static final String FUNDS_LOG_PATH = BASE_LOG_PATH + "funds.csv";
        public static final String ACTIVITY_LOG_PATH = BASE_LOG_PATH + "activity.log";
        public static final String EVENT_LOG_PATH = BASE_LOG_PATH + "events/";

        public static final String INFO_LOG_FILE = "info.json";
        public static final String STATUS_LOG_FILE = "status.csv";
        public static final String PRICES_LOG_FILE = "prices.csv";
        public static final String ACTIONS_LOG_FILE = "actions.csv";
    }

    public static class Execution
    {
        public static final String IS_RUNNING_FLAG = "running.txt";
    }
}