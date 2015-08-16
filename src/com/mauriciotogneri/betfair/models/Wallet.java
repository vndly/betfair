package com.mauriciotogneri.betfair.models;

import com.mauriciotogneri.betfair.logs.WalletLog;

import java.io.IOException;

public class Wallet
{
    private double balance = 0;

    private static Wallet instance = null;

    public enum Type
    {
        WITHDRAW, //
        DEPOSIT, //
        FAIL
    }

    private static final double DEFAULT_BALANCE = 50.00;

    private Wallet(double balance) throws IOException
    {
        this.balance = balance;
    }

    public static synchronized Wallet getInstance() throws IOException
    {
        if (instance == null)
        {
            instance = new Wallet(DEFAULT_BALANCE);
        }

        return instance;
    }

    public synchronized boolean withdraw(Budget budget, String eventId, String marketId, String player) throws IOException
    {
        if (balance >= budget.getRequested())
        {
            balance -= budget.getRequested();

            WalletLog.log(Type.WITHDRAW, budget.getId(), eventId, marketId, player, budget.getRequested(), balance);

            return true;
        }
        else
        {
            WalletLog.log(Type.FAIL, budget.getId(), eventId, marketId, player, budget.getRequested(), balance);
        }

        return false;
    }

    public synchronized void deposit(Budget budget, String eventId, String marketId, String player, double profit) throws IOException
    {
        balance += profit;

        WalletLog.log(Type.DEPOSIT, budget.getId(), eventId, marketId, player, profit, balance);
    }
}