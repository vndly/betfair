package com.mauriciotogneri.kernel.api;

import com.google.gson.Gson;
import com.mauriciotogneri.kernel.api.base.BaseRequest;
import com.mauriciotogneri.kernel.api.base.Enums.MarketProjection;
import com.mauriciotogneri.kernel.api.base.Enums.MarketSort;
import com.mauriciotogneri.kernel.api.base.ListCallParameters;
import com.mauriciotogneri.kernel.api.base.Types.MarketCatalogue;
import com.mauriciotogneri.kernel.api.base.Types.MarketFilter;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListMarketCatalogue extends BaseRequest<ListMarketCatalogue.Response, ListMarketCatalogue.Parameters>
{
    public ListMarketCatalogue(OkHttpClient client, Gson gson, String appKey, String sessionToken)
    {
        super(client, gson, appKey, sessionToken);
    }

    @Override
    protected Class<Response> getClassType()
    {
        return Response.class;
    }

    public Response execute() throws IOException
    {
        return execute(new ListMarketCatalogue.Parameters());
    }

    public Response execute(ListMarketCatalogue.Parameters parameters) throws IOException
    {
        return execute("listMarketCatalogue", parameters);
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
}