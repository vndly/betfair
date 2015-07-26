package com.mauriciotogneri.kernel.models;

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

    public Selection getLowestBack()
    {
        Selection result = null;

        for (Selection selection : selections)
        {
            if ((result == null) || (selection.back < result.back))
            {
                result = selection;
            }
        }

        return result;
    }

    public boolean allBackAvailable()
    {
        for (Selection selection : selections)
        {
            if (selection.back == 0)
            {
                return false;
            }
        }

        return true;
    }

    public double getLayPrice(long selectionId)
    {
        for (Selection selection : selections)
        {
            if (selection.id == selectionId)
            {
                return selection.lay;
            }
        }

        return 0;
    }
}