package com.mauriciotogneri.kernel.models;

import java.util.ArrayList;
import java.util.List;

public class Tick
{
    public long timestamp;
    public List<Selection> selections = new ArrayList<>();

    public Tick(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void add(Selection selection)
    {
        selections.add(selection);
    }
}