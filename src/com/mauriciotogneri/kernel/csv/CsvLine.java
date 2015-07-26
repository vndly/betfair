package com.mauriciotogneri.kernel.csv;

import com.mauriciotogneri.kernel.utils.TimeFormatter;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

public class CsvLine
{
    private final StringBuilder builder = new StringBuilder();
    private boolean firstElement = true;

    private static final String SEPARATOR = ",";
    private static final PeriodFormatter periodFormatter = TimeFormatter.getPeriodFormatter();

    public CsvLine appendTimestamp(long timestamp)
    {
        if (timestamp > 0)
        {
            Period period = new Period(timestamp);

            return append(periodFormatter.print(period));
        }
        else
        {
            Period period = new Period(Math.abs(timestamp));

            return append("-" + periodFormatter.print(period));
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