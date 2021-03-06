package com.mauriciotogneri.betfair.api.betting;

import com.mauriciotogneri.betfair.api.base.BettingRequest;
import com.mauriciotogneri.betfair.api.base.Enums.MatchProjection;
import com.mauriciotogneri.betfair.api.base.Enums.OrderProjection;
import com.mauriciotogneri.betfair.api.base.Enums.PriceData;
import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.api.base.Types.ExBestOffersOverrides;
import com.mauriciotogneri.betfair.api.base.Types.MarketBook;
import com.mauriciotogneri.betfair.api.base.Types.PriceProjection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListMarketBook extends BettingRequest<ListMarketBook.Response, ListMarketBook.Parameters>
{
    public ListMarketBook(HttpClient httpClient, Session session)
    {
        super(httpClient, session);
    }

    @Override
    protected Class<Response> getClassType()
    {
        return Response.class;
    }

    @Override
    protected String getMethod()
    {
        return "listMarketBook";
    }

    public static class Parameters
    {
        private final List<String> marketIds;
        private final PriceProjection priceProjection;
        private final OrderProjection orderProjection;
        private final MatchProjection matchProjection;

        public Parameters(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection, MatchProjection matchProjection)
        {
            this.marketIds = marketIds;
            this.priceProjection = priceProjection;
            this.orderProjection = orderProjection;
            this.matchProjection = matchProjection;
        }

        public static class Builder
        {
            private final List<String> marketIds;
            private PriceProjection priceProjection;
            private OrderProjection orderProjection;
            private MatchProjection matchProjection;

            public Builder(List<String> marketIds)
            {
                this.marketIds = marketIds;
            }

            public Builder(String... ids)
            {
                this.marketIds = new ArrayList<>(Arrays.asList(ids));
            }

            public void setPriceProjection(PriceProjection priceProjection)
            {
                this.priceProjection = priceProjection;
            }

            public void setOrderProjection(OrderProjection orderProjection)
            {
                this.orderProjection = orderProjection;
            }

            public void setMatchProjection(MatchProjection matchProjection)
            {
                this.matchProjection = matchProjection;
            }

            public Parameters build()
            {
                return new Parameters(marketIds, priceProjection, orderProjection, matchProjection);
            }
        }
    }

    public static class Response extends ArrayList<MarketBook>
    {
    }

    public static ListMarketBook getRequest(HttpClient httpClient, Session session, String marketId)
    {
        PriceProjection priceProjection = new PriceProjection(PriceData.EX_BEST_OFFERS);
        priceProjection.virtualise = true;
        priceProjection.exBestOffersOverrides = new ExBestOffersOverrides();
        priceProjection.exBestOffersOverrides.bestPricesDepth = 1;

        Parameters.Builder parameters = new Parameters.Builder(marketId);
        parameters.setPriceProjection(priceProjection);

        ListMarketBook listMarketCatalogue = new ListMarketBook(httpClient, session);
        listMarketCatalogue.setParameters(parameters.build());

        return listMarketCatalogue;
    }
}