package com.mauriciotogneri.kernel.strategies;

import com.mauriciotogneri.kernel.models.Tick;

public abstract class Strategy
{
    public abstract void process(Tick tick) throws Exception;
}