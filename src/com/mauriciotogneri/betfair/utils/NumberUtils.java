package com.mauriciotogneri.betfair.utils;

import java.text.DecimalFormat;

public class NumberUtils
{
    private static final int DEFAULT_DECIMAL_PLACES = 3;
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    public static double round(double value, int decimals)
    {
        int base = (int) Math.pow(10, decimals);

        return Math.floor(value * base) / base;
    }

    public static double round(double value)
    {
        return round(value, DEFAULT_DECIMAL_PLACES);
    }

    public static synchronized String format(double value)
    {
        return decimalFormat.format(value);
    }
}