package com.mauriciotogneri.betfair.models;

import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.logs.WalletLog;
import com.mauriciotogneri.betfair.utils.NumberUtils;

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

    private static final double DEFAULT_BALANCE = 10.00;

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

    public synchronized boolean withdraw(Budget budget, String eventId, String marketId) throws IOException
    {
        if (balance >= budget.getRequested())
        {
            balance -= budget.getRequested();

            log(Type.WITHDRAW, budget.getId(), eventId, marketId, budget.getRequested(), balance);

            return true;
        }
        else
        {
            log(Type.FAIL, budget.getId(), eventId, marketId, budget.getRequested(), balance);
        }

        return false;
    }

    public synchronized void deposit(Budget budget, String eventId, String marketId, double profit) throws IOException
    {
        balance += profit;

        log(Type.DEPOSIT, budget.getId(), eventId, marketId, profit, balance);
    }

    private synchronized void log(Type type, int budgetId, String eventId, String marketId, double value, double balance) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendCurrentTimestamp();
        csvLine.append(type.toString());
        csvLine.append(budgetId);
        csvLine.append(eventId);
        csvLine.append(marketId);
        csvLine.append(NumberUtils.round(value, 2));
        csvLine.append(NumberUtils.round(balance, 2));

        WalletLog.log(csvLine);
    }
}