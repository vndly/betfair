package com.mauriciotogneri.betfair.api.betting;

import com.mauriciotogneri.betfair.api.base.BettingRequest;
import com.mauriciotogneri.betfair.api.base.Enums.OrderType;
import com.mauriciotogneri.betfair.api.base.Enums.PersistenceType;
import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.api.base.Types.LimitOrder;
import com.mauriciotogneri.betfair.api.base.Types.PlaceExecutionReport;
import com.mauriciotogneri.betfair.api.base.Types.PlaceInstruction;
import com.mauriciotogneri.betfair.api.betting.PlaceOrders.Parameters;
import com.mauriciotogneri.betfair.models.BetInstruction;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrders extends BettingRequest<PlaceExecutionReport, Parameters>
{
    public PlaceOrders(HttpClient httpClient, Session session)
    {
        super(httpClient, session);
    }

    @Override
    protected Class<PlaceExecutionReport> getClassType()
    {
        return PlaceExecutionReport.class;
    }

    @Override
    protected String getMethod()
    {
        return "placeOrders";
    }

    public static class Parameters
    {
        public String marketId;
        public List<PlaceInstruction> instructions;
        public String customerRef;

        public Parameters(String marketId, PlaceInstruction instruction, String customerRef)
        {
            this.marketId = marketId;

            this.instructions = new ArrayList<>();
            this.instructions.add(instruction);

            this.customerRef = customerRef;
        }
    }

    public static PlaceOrders getRequest(HttpClient httpClient, Session session, BetInstruction betInstruction)
    {
        LimitOrder limitOder = new LimitOrder(betInstruction.stake, betInstruction.price, PersistenceType.PERSIST);

        PlaceInstruction placeInstruction = new PlaceInstruction(OrderType.LIMIT, betInstruction.selectionId, betInstruction.side, limitOder);

        Parameters parameters = new Parameters(betInstruction.marketId, placeInstruction, betInstruction.getRef());

        PlaceOrders placeOrders = new PlaceOrders(httpClient, session);
        placeOrders.setParameters(parameters);

        return placeOrders;
    }
}