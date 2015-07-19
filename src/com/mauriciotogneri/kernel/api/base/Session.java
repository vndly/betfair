package com.mauriciotogneri.kernel.api.base;

public class Session
{
    public final String appKey;
    public final String sessionToken;

    public Session(String appKey, String sessionToken)
    {
        this.appKey = appKey;
        this.sessionToken = sessionToken;
    }
}