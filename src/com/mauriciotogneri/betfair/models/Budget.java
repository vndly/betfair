package com.mauriciotogneri.betfair.models;

public class Budget
{
    private final double requested;
    private double used = 0;

    public Budget(double requested)
    {
        this.requested = requested;
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
}