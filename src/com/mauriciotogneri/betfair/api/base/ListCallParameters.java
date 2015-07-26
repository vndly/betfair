package com.mauriciotogneri.betfair.api.base;

import com.mauriciotogneri.betfair.api.base.Types.MarketFilter;

@SuppressWarnings("unused")
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