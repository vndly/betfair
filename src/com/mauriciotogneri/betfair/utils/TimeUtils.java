package com.mauriciotogneri.betfair.utils;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils
{
    private static final DateFormat logTimestamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
    private static final DateFormat fullTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static synchronized String getTimestamp()
    {
        return logTimestamp.format(new Date());
    }

    public static synchronized long dateToMilliseconds(String timestamp, String timeZone) throws ParseException
    {
        fullTimestamp.setTimeZone(TimeZone.getTimeZone(timeZone));

        return fullTimestamp.parse(timestamp).getTime();
    }

    public static PeriodFormatter getPeriodFormatter()
    {
        PeriodFormatterBuilder periodFormatterBuilder = new PeriodFormatterBuilder();
        periodFormatterBuilder.printZeroAlways();
        periodFormatterBuilder.minimumPrintedDigits(2);
        periodFormatterBuilder.appendHours();
        periodFormatterBuilder.appendSeparator(":");
        periodFormatterBuilder.printZeroAlways();
        periodFormatterBuilder.minimumPrintedDigits(2);
        periodFormatterBuilder.appendMinutes();
        periodFormatterBuilder.appendSeparator(":");
        periodFormatterBuilder.printZeroAlways();
        periodFormatterBuilder.minimumPrintedDigits(2);
        periodFormatterBuilder.appendSeconds();
        periodFormatterBuilder.appendSeparator(".");
        periodFormatterBuilder.printZeroAlways();
        periodFormatterBuilder.minimumPrintedDigits(3);
        periodFormatterBuilder.appendMillis();

        return periodFormatterBuilder.toFormatter();
    }
}