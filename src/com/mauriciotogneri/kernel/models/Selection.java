package com.mauriciotogneri.kernel.models;

public class Selection
{
    public long id;
    public double back;
    public double lay;

    public Selection(long id, double back, double lay)
    {
        this.id = id;
        this.back = back;
        this.lay = lay;
    }
}