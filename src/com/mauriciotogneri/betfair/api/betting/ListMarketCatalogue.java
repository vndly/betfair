package com.mauriciotogneri.betfair.api.betting;

import com.mauriciotogneri.betfair.api.base.BettingRequest;
import com.mauriciotogneri.betfair.api.base.Enums.MarketProjection;
import com.mauriciotogneri.betfair.api.base.Enums.MarketSort;
import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.api.base.ListCallParameters;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.betfair.api.base.Types.MarketFilter;
import com.mauriciotogneri.betfair.api.base.Types.MarketFilter.Builder;
import com.mauriciotogneri.betfair.api.betting.ListMarketCatalogue.Parameters;
import com.mauriciotogneri.betfair.api.betting.ListMarketCatalogue.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListMarketCatalogue extends BettingRequest<Response, Parameters>
{
    public ListMarketCatalogue(HttpClient httpClient, Session session)
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
        return "listMarketCatalogue";
    }

    public static class Parameters extends ListCallParameters
    {
        private final List<MarketProjection> marketProjection;
        private final MarketSort sort;
        private final Integer maxResults;

        public Parameters()
        {
            super();

            this.marketProjection = null;
            this.sort = null;
            this.maxResults = null;
        }

        public Parameters(MarketFilter filter, List<MarketProjection> marketProjection, MarketSort sort, Integer maxResults)
        {
            super(filter);

            this.marketProjection = marketProjection;
            this.sort = sort;
            this.maxResults = maxResults;
        }

        public static class Builder
        {
            private final MarketFilter filter;
            private List<MarketProjection> marketProjection;
            private MarketSort sort;
            private Integer maxResults;

            public Builder(MarketFilter filter)
            {
                this.filter = filter;
            }

            public void setMarketProjection(List<MarketProjection> marketProjection)
            {
                this.marketProjection = marketProjection;
            }

            public void setMarketProjection(MarketProjection... projections)
            {
                this.marketProjection = new ArrayList<>(Arrays.asList(projections));
            }

            public void setSort(MarketSort sort)
            {
                this.sort = sort;
            }

            public void setMaxResults(Integer maxResults)
            {
                this.maxResults = maxResults;
            }

            public Parameters build()
            {
                return new Parameters(filter, marketProjection, sort, maxResults);
            }
        }
    }

    public static class Response extends ArrayList<MarketCatalogue>
    {
    }

    public static ListMarketCatalogue getRequest(HttpClient httpClient, Session session, String eventId, String... marketTypes)
    {
        MarketFilter.Builder marketFilter = new Builder();
        marketFilter.setEventIds(eventId);
        marketFilter.setMarketTypeCodes(marketTypes);

        ListMarketCatalogue.Parameters.Builder parameters = new ListMarketCatalogue.Parameters.Builder(marketFilter.build());
        parameters.setMarketProjection(MarketProjection.MARKET_DESCRIPTION, MarketProjection.RUNNER_DESCRIPTION, MarketProjection.RUNNER_METADATA);
        parameters.setMaxResults(1000);

        ListMarketCatalogue listMarketCatalogue = new ListMarketCatalogue(httpClient, session);
        listMarketCatalogue.setParameters(parameters.build());

        return listMarketCatalogue;
    }

    public static ListMarketCatalogue.Response get(HttpClient httpClient, Session session, String eventId, String... marketTypes) throws IOException
    {
        ListMarketCatalogue listMarketCatalogue = getRequest(httpClient, session, eventId, marketTypes);

        return listMarketCatalogue.execute();
    }
}