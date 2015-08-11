package com.mauriciotogneri.betfair.csv;

import com.mauriciotogneri.betfair.utils.TimeUtils;

public class CsvLine
{
    private final StringBuilder builder = new StringBuilder();
    private boolean firstElement = true;

    private static final String SEPARATOR = ",";

    public CsvLine appendCurrentTimestamp()
    {
        append(TimeUtils.getTimestamp());

        return this;
    }

    public CsvLine appendTimestamp(long timestamp)
    {
        if (timestamp >= 0)
        {
            return append(TimeUtils.getPeriod(timestamp));
        }
        else
        {
            return append("-" + TimeUtils.getPeriod(timestamp));
        }
    }

    public CsvLine append(String value)
    {
        if (!firstElement)
        {
            separator();
        }

        builder.append(value);

        firstElement = false;

        return this;
    }

    public CsvLine append(long value)
    {
        return append(String.valueOf(value));
    }

    public CsvLine append(double value)
    {
        return append(String.valueOf(value));
    }

    public CsvLine append(boolean value)
    {
        return append(String.valueOf(value));
    }

    public CsvLine separator()
    {
        builder.append(SEPARATOR);

        return this;
    }

    public String toString()
    {
        return builder.toString();
    }
}