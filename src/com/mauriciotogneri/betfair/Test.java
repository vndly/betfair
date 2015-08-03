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
        DateFormat fullTimestamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        fullTimestamp.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));

        System.out.print(fullTimestamp.format(new Date()));
    }
}