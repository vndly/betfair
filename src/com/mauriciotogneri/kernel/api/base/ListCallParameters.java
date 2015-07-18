package com.mauriciotogneri.kernel.api.base;

import com.mauriciotogneri.kernel.api.base.Types.MarketFilter;

public class ListCallParameters
{
    private final MarketFilter filter;

    private static final MarketFilter emptyFilter = new MarketFilter.Builder().build();

    public ListCallParameters()
    {
        this(emptyFilter);
    }

    public ListCallParameters(MarketFilter filter)
    {
        this.filter = filter;
    }
}