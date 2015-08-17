package com.mauriciotogneri.betfair.tools;

import com.mauriciotogneri.betfair.models.BetSimulation;
import com.mauriciotogneri.betfair.models.Tick;
import com.mauriciotogneri.betfair.strategies.kernel.StrategyTennisMatchOdds;

import java.io.IOException;

public class StrategyTennisMatchOddsSimulation extends StrategyTennisMatchOdds
{
    private BetSimulation betSimulationPlayerA;
    private BetSimulation betSimulationPlayerB;

    @Override
    protected void onTick(Tick tick) throws IOException
    {
    }

    @Override
    protected boolean onRequestBudget(Player player, double value) throws IOException
    {
        return true;
    }

    @Override
    protected void onUseBudget(Player player, double value) throws IOException
    {
    }

    @Override
    protected void onReturnBudget(Player player) throws IOException
    {
    }

    @Override
    protected boolean onPlaceBackBet(Player player, double price, double stake, long timestamp) throws IOException
    {
        return true;
    }

    @Override
    protected boolean onPlaceLayBet(Player player, double price, long timestamp) throws IOException
    {
        return true;
    }

    @Override
    protected void onLayPriceLow(Player player, long timestamp, double value) throws IOException
    {
    }

    @Override
    protected void onClose(Player player, boolean executed, long timestamp, BetSimulation betSimulation) throws IOException
    {
        if (player == Player.PLAYER_A)
        {
            betSimulationPlayerA = betSimulation;
        }
        else if (player == Player.PLAYER_B)
        {
            betSimulationPlayerB = betSimulation;
        }
    }

    public BetSimulation getBetSimulationPlayerA()
    {
        return betSimulationPlayerA;
    }

    public BetSimulation getBetSimulationPlayerB()
    {
        return betSimulationPlayerB;
    }
}