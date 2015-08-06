package com.mauriciotogneri.betfair.monitors;

import com.mauriciotogneri.betfair.api.accounts.GetAccountFunds;
import com.mauriciotogneri.betfair.api.accounts.GetAccountFunds.AccountFundsResponse;
import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.logs.FundsLog;

public class FundsMonitor extends AbstractMonitor
{
    private final Session session;
    private final GetAccountFunds getAccountFunds;

    private static final int WAITING_TIME = 60 * 1000; // one minute (in milliseconds)

    public FundsMonitor(HttpClient httpClient, Session session)
    {
        super(httpClient, session);

        this.session = session;
        this.getAccountFunds = new GetAccountFunds(HttpClient.getDefault());
    }

    @Override
    protected int getWaitTime()
    {
        return WAITING_TIME;
    }

    @Override
    protected boolean execute() throws Exception
    {
        AccountFundsResponse accountFundsResponse = getAccountFunds.execute(session.appKey, session.getSessionToken());

        FundsLog.log(accountFundsResponse.availableToBetBalance);

        return true;
    }
}