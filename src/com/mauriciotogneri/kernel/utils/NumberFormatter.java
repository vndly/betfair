package com.mauriciotogneri.kernel.utils;

public class NumberFormatter
{
    public static double round(double value, int decimals)
    {
        int base = (int) Math.pow(10, decimals);

        return Math.floor(value * base) / base;
    }
}