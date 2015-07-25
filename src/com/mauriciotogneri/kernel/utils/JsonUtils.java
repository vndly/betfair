package com.mauriciotogneri.kernel.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mauriciotogneri.kernel.logs.ErrorLog;

public class JsonUtils
{
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Object object)
    {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz)
    {
        try
        {
            return gson.fromJson(json, clazz);
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