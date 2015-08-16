package com.mauriciotogneri.betfair.models;

import java.util.ArrayList;
import java.util.List;

public class Tick
{
    public final long timestamp;
    public final List<Selection> selections = new ArrayList<>();

    public Tick(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void add(Selection selection)
    {
        selections.add(selection);
    }
}