package com.mauriciotogneri.betfair.utils;

import com.mauriciotogneri.betfair.Constants.Email;
import com.mauriciotogneri.betfair.logs.ErrorLog;

public class NotificationUtils
{
    public static void sendNotificationFinished()
    {
        sendEmail("FINISHED");
    }

    public static void sendNotificationFunds(double value)
    {
        sendEmail("FUNDS: " + value);
    }

    private static void sendEmail(String subject, String content)
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder("./scripts/sendmail.sh", Email.SENDER_EMAIL, Email.RECEIVER_EMAIL, subject, content);
            processBuilder.start();
        }
        catch (Exception e)
        {
            ErrorLog.log(e);
        }
    }

    private static void sendEmail(String subject)
    {
        sendEmail(subject, "");
    }
}