package com.mauriciotogneri.kernel.utils;

public class NumberUtils
{
    private static final int DEFAULT_DECIMAL_PLACES = 3;

    public static double round(double value, int decimals)
    {
        int base = (int) Math.pow(10, decimals);

        return Math.floor(value * base) / base;
    }

    public static double round(double value)
    {
        return round(value, DEFAULT_DECIMAL_PLACES);
    }
}