package com.mauriciotogneri.kernel.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;

public class JsonUtils
{
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Object object)
    {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz)
    {
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(Reader reader, Class<T> clazz)
    {
        return gson.fromJson(reader, clazz);
    }
}