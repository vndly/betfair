package com.mauriciotogneri.betfair.api.betting;

import com.mauriciotogneri.betfair.api.base.BettingRequest;
import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.api.base.Types.CancelExecutionReport;
import com.mauriciotogneri.betfair.api.base.Types.CancelInstruction;
import com.mauriciotogneri.betfair.api.betting.CancelOrders.Parameters;
import com.mauriciotogneri.betfair.models.Bet;

import java.util.ArrayList;
import java.util.List;

public class CancelOrders extends BettingRequest<CancelExecutionReport, Parameters>
{
    public CancelOrders(HttpClient httpClient, Session session)
    {
        super(httpClient, session);
    }

    @Override
    protected Class<CancelExecutionReport> getClassType()
    {
        return CancelExecutionReport.class;
    }

    @Override
    protected String getMethod()
    {
        return "cancelOrders";
    }

    public static class Parameters
    {
        public String marketId;
        public List<CancelInstruction> instructions;
        public String customerRef;

        public Parameters(String marketId, CancelInstruction instruction, String customerRef)
        {
            this.marketId = marketId;

            this.instructions = new ArrayList<>();
            this.instructions.add(instruction);

            this.customerRef = customerRef;
        }
    }

    public static CancelOrders getRequest(HttpClient httpClient, Session session, Bet bet)
    {
        CancelInstruction cancelInstruction = new CancelInstruction(bet.id);

        Parameters parameters = new Parameters(bet.marketId, cancelInstruction, bet.getRef());

        CancelOrders cancelOrders = new CancelOrders(httpClient, session);
        cancelOrders.setParameters(parameters);

        return cancelOrders;
    }
}