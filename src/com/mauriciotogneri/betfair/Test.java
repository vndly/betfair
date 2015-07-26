package com.mauriciotogneri.betfair;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Test
{
    public static void main(String[] args) throws ParseException
    {
        String timestamp = "2015-07-19T22:30:00.000Z";

        DateFormat fullTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        fullTimestamp.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = fullTimestamp.parse(timestamp);

        System.out.print(date);
    }
}