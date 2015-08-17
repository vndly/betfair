package com.mauriciotogneri.betfair.utils;

public class StringUtils
{
    public static boolean isEmpty(String a)
    {
        return (a == null) || (a.equals(""));
    }

    public static boolean equals(String a, String b)
    {
        return (a != null) && (a.equals(b));
    }

    public static boolean notEquals(String a, String b)
    {
        return !equals(a, b);
    }

    public static boolean contains(String a, String b)
    {
        return (a != null) && a.contains(b);
    }
}