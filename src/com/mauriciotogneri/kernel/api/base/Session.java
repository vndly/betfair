package com.mauriciotogneri.kernel.api.base;

public class Session
{
    public final String appKey;
    private String sessionToken;

    public Session(String appKey, String sessionToken)
    {
        this.appKey = appKey;
        this.sessionToken = sessionToken;
    }

    public synchronized String getSessionToken()
    {
        return sessionToken;
    }

    public synchronized void setSessionToken(String token)
    {
        sessionToken = token;
    }
}