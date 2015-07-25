package com.mauriciotogneri.kernel.utils;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeFormatter
{
    private static final DateFormat timestampFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static String getTimestamp()
    {
        return timestampFormat.format(new Date());
    }

    public static long dateToMilliseconds(String timestamp, String timeZone) throws ParseException
    {
        DateFormat fullTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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

        return periodFormatterBuilder.toFormatter();
    }
}