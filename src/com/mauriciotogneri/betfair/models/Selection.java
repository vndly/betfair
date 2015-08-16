package com.mauriciotogneri.betfair.models;

public class Selection
{
    public final double back;
    public final double lay;

    public Selection(double back, double lay)
    {
        this.back = back;
        this.lay = lay;
    }
}