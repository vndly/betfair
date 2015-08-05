package com.mauriciotogneri.betfair.models;

public class Budget
{
    private final int id;
    private final double requested;
    private double used = 0;

    private static int NEXT_ID = 1;

    public Budget(double requested)
    {
        this.id = getNextId();
        this.requested = requested;
    }

    public int getId()
    {
        return id;
    }

    public synchronized double getRequested()
    {
        return requested;
    }

    public synchronized void use(double value)
    {
        used += value;
    }

    public synchronized double getRest()
    {
        return requested - used;
    }

    public static synchronized int getNextId()
    {
        return NEXT_ID++;
    }
}