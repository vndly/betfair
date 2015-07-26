package com.mauriciotogneri.betfair.models;

public class Selection
{
    public final long id;
    public final double back;
    public final double lay;

    public Selection(long id, double back, double lay)
    {
        this.id = id;
        this.back = back;
        this.lay = lay;
    }
}