package com.mauriciotogneri.betfair.models;

public class Selection
{
    public final long id;
    public final double back;
    public final double lay;
    public final int index;

    public Selection(long id, double back, double lay, int index)
    {
        this.id = id;
        this.back = back;
        this.lay = lay;
        this.index = index;
    }
}