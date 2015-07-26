package com.mauriciotogneri.kernel;

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
        public static final String ERROR_LOG_PATH = "logs/error.log";
    }
}