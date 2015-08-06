package com.mauriciotogneri.betfair.models;

import com.mauriciotogneri.betfair.Constants;
import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.utils.NumberUtils;

import java.io.IOException;

public class Wallet
{
    private double balance = 0;
    private final CsvFile log;

    private static Wallet instance = null;

    private static final double DEFAULT_BALANCE = 50.00;

    private Wallet(double balance) throws IOException
    {
        this.balance = balance;
        this.log = new CsvFile(Constants.Log.WALLET_LOG_PATH);
    }

    public static synchronized Wallet getInstance() throws IOException
    {
        if (instance == null)
        {
            instance = new Wallet(DEFAULT_BALANCE);
        }

        return instance;
    }

    public synchronized boolean requestBudget(Budget budget) throws IOException
    {
        if (balance >= budget.getRequested())
        {
            balance -= budget.getRequested();

            log("REQUEST OK", budget.getId(), budget.getRequested(), balance);

            return true;
        }
        else
        {
            log("REQUEST FAIL", budget.getId(), budget.getRequested(), balance);
        }

        return false;
    }

    public synchronized void addProfit(int budgetId, double profit) throws IOException
    {
        balance += profit;

        log("PROFIT", budgetId, profit, balance);
    }

    private synchronized void log(String type, int budgetId, double value, double balance) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendCurrentTimestamp();
        csvLine.append(type);
        csvLine.append(budgetId);
        csvLine.append(NumberUtils.round(value, 2));
        csvLine.append(NumberUtils.round(balance, 2));

        log.write(csvLine);
    }
}