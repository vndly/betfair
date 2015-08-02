package com.mauriciotogneri.betfair.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mauriciotogneri.betfair.logs.ErrorLog;

public class JsonUtils
{
    private static final Gson gsonNormal = new Gson();
    private static final Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Object object, boolean pretty)
    {
        if (pretty)
        {
            return gsonPretty.toJson(object);
        }
        else
        {
            return gsonNormal.toJson(object);
        }
    }

    public static String toJson(Object object)
    {
        return toJson(object, true);
    }

    public static <T> T fromJson(String json, Class<T> clazz)
    {
        try
        {
            return gsonPretty.fromJson(json, clazz);
        }
        catch (Exception e)
        {
            ErrorLog.log("ERROR CONVERTING JSON TO CLASS: " + clazz.getCanonicalName());
            ErrorLog.log(json);
            ErrorLog.log(e);

            throw e;
        }
    }
}