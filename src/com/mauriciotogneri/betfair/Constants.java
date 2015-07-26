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
        public static final String EVENT_LOG_PATH = BASE_LOG_PATH + "events/";

        public static final String INFO_LOG_FILE = "info.json";
        public static final String STATUS_LOG_FILE = "status.csv";
        public static final String PRICES_LOG_FILE = "prices.csv";
        public static final String ACTIONS_LOG_FILE = "actions.csv";
    }
}