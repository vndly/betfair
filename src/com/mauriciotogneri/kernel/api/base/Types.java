package com.mauriciotogneri.kernel.api.base;

import com.mauriciotogneri.kernel.api.base.Enums.MarketStatus;
import com.mauriciotogneri.kernel.api.base.Enums.PriceData;
import com.mauriciotogneri.kernel.api.base.Enums.RollupModel;
import com.mauriciotogneri.kernel.api.base.Enums.RunnerStatus;
import com.mauriciotogneri.kernel.utils.NumberFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Types
{
    public static class EventResult
    {
        public Event event = null;
        public int marketCount = 0;

        public boolean isValid()
        {
            return (event != null);
        }
    }

    public static class Event
    {
        public String id = "";
        public String name = "";
        public String countryCode = "";
        public String timezone = "";
        public String venue = "";
        public String openDate = "";
    }

    public static class EventType
    {
        public String id = "";
        public String name = "";
    }

    public static class Competition
    {
        public String id = "";
        public String name = "";
    }

    public static class Runner
    {
        public long selectionId = 0;
        public double handicap = 0;
        public RunnerStatus status = null;
        public double adjustmentFactor = 0;
        public double lastPriceTraded = 0;
        public double totalMatched = 0;
        public String removalDate = "";
        public StartingPrices sp = null;
        public ExchangePrices ex = null;
        //public List<Object> orders = new ArrayList<>();
        //public List<Object> matches = new ArrayList<>();

        public boolean isActive()
        {
            return (status != null) && (status == RunnerStatus.ACTIVE);
        }

        public PriceSize getBackValue()
        {
            if ((ex != null) && (!ex.availableToBack.isEmpty()))
            {
                return ex.availableToBack.get(0);
            }

            return null;
        }

        public PriceSize getLayValue()
        {
            if ((ex != null) && (!ex.availableToLay.isEmpty()))
            {
                return ex.availableToLay.get(0);
            }

            return null;
        }
    }

    public static class PriceSize
    {
        public double price = 0;
        public double size = 0;
    }

    public static class StartingPrices
    {
        public double nearPrice = 0;
        public double farPrice = 0;
        public List<PriceSize> backStakeTaken = new ArrayList<>();
        public List<PriceSize> layLiabilityTaken = new ArrayList<>();
        public double actualSP = 0;
    }

    public static class ExchangePrices
    {
        public List<PriceSize> availableToBack = new ArrayList<>();
        public List<PriceSize> availableToLay = new ArrayList<>();
        public List<PriceSize> tradedVolume = new ArrayList<>();

        public double getMinimumPrice()
        {
            double result = 0;
            double min = Double.MAX_VALUE;

            for (PriceSize priceSize : availableToBack)
            {
                if (priceSize.price < min)
                {
                    min = priceSize.price;
                    result = priceSize.price;
                }
            }

            return NumberFormatter.round(result, 3);
        }

        public double getMaximumPrice()
        {
            double result = 0;
            double max = Double.MIN_VALUE;

            for (PriceSize priceSize : availableToBack)
            {
                if (priceSize.price > max)
                {
                    max = priceSize.price;
                    result = priceSize.price;
                }
            }

            return NumberFormatter.round(result, 3);
        }

        public double getAveragePrice()
        {
            double sum = 0;
            double length = availableToBack.size();

            for (PriceSize priceSize : availableToBack)
            {
                sum += priceSize.price;
            }

            return NumberFormatter.round((length == 0) ? 0 : (sum / length), 3);
        }
    }

    public static class RunnerCatalog
    {
        public long selectionId = 0;
        public String runnerName = "";
        public double handicap = 0;
        public int sortPriority = 0;
        public Map<String, String> metadata = new HashMap<>();
    }

    public static class PriceProjection
    {
        public List<PriceData> priceData;
        public ExBestOffersOverrides exBestOffersOverrides;
        public boolean virtualise;
        public boolean rolloverStakes;

        public PriceProjection()
        {
        }

        public PriceProjection(List<PriceData> priceData)
        {
            this.priceData = priceData;
        }

        public PriceProjection(PriceData... data)
        {
            this.priceData = new ArrayList<>(Arrays.asList(data));
        }
    }

    public static class ExBestOffersOverrides
    {
        public Integer bestPricesDepth;
        public RollupModel rollupModel;
        public Integer rollupLimit;
        public Double rollupLiabilityThreshold;
        public Integer rollupLiabilityFactor;
    }

    public static class MarketCatalogue
    {
        public String marketId = "";
        //public String marketName = "";
        //public String marketStartTime = "";
        public MarketDescription description = null;
        //public double totalMatched = 0;
        //public List<RunnerCatalog> runners = new ArrayList<>();
        //public EventType eventType = null;
        //public Competition competition = null;
        //public Event event = null;
    }

    public static class MarketBook
    {
        public String marketId = "";
        public boolean isMarketDataDelayed = false;
        public MarketStatus status;
        public int betDelay = 0;
        public boolean bspReconciled = false;
        public boolean complete = false;
        public boolean inplay = false;
        public int numberOfWinners = 0;
        public int numberOfRunners = 0;
        public int numberOfActiveRunners = 0;
        public String lastMatchTime = "";
        public double totalMatched = 0;
        public double totalAvailable = 0;
        public boolean crossMatching = false;
        public boolean runnersVoidable = false;
        public long version = 0;
        public List<Runner> runners = new ArrayList<>();

        public Runner getRunner(long selectionId)
        {
            for (Runner runner : runners)
            {
                if (runner.selectionId == selectionId)
                {
                    return runner;
                }
            }

            return null;
        }
    }

    public static class MarketDescription
    {
        //public boolean persistenceEnabled = false;
        //public boolean bspMarket = false;
        //public String marketTime = "";
        //public String suspendTime = "";
        //public String settleTime = "";
        //public MarketBettingType bettingType = null;
        //public boolean turnInPlayEnabled = false;
        public String marketType = "";
        //public String regulator = "";
        //public double marketBaseRate = 0;
        //public boolean discountAllowed = false;
        //public String wallet = "";
        //public String rules = "";
        //public boolean rulesHasDate = false;
        //public double eachWayDivisor = 0;
        //public String clarifications = "";
    }

    public static class MarketFilter
    {
        private final String textQuery;
        private final List<String> exchangeIds;
        private final List<String> eventTypeIds;
        private final List<String> eventIds;
        private final List<String> competitionIds;
        private final List<String> marketIds;
        private final List<String> venues;
        private final Boolean bspOnly;
        private final Boolean turnInPlayEnabled;
        private final Boolean inPlayOnly;
        private final List<String> marketBettingTypes;
        private final List<String> marketCountries;
        private final List<String> marketTypeCodes;
        private final String marketStartTime;
        private final List<String> withOrders;

        private MarketFilter(String textQuery, List<String> exchangeIds, List<String> eventTypeIds, List<String> eventIds, List<String> competitionIds, List<String> marketIds, List<String> venues, Boolean bspOnly, Boolean turnInPlayEnabled, Boolean inPlayOnly, List<String> marketBettingTypes, List<String> marketCountries, List<String> marketTypeCodes, String marketStartTime, List<String> withOrders)
        {
            this.textQuery = textQuery;
            this.exchangeIds = exchangeIds;
            this.eventTypeIds = eventTypeIds;
            this.eventIds = eventIds;
            this.competitionIds = competitionIds;
            this.marketIds = marketIds;
            this.venues = venues;
            this.bspOnly = bspOnly;
            this.turnInPlayEnabled = turnInPlayEnabled;
            this.inPlayOnly = inPlayOnly;
            this.marketBettingTypes = marketBettingTypes;
            this.marketCountries = marketCountries;
            this.marketTypeCodes = marketTypeCodes;
            this.marketStartTime = marketStartTime;
            this.withOrders = withOrders;
        }

        public static class Builder
        {
            private String textQuery;
            private List<String> exchangeIds;
            private List<String> eventTypeIds;
            private List<String> eventIds;
            private List<String> competitionIds;
            private List<String> marketIds;
            private List<String> venues;
            private Boolean bspOnly;
            private Boolean turnInPlayEnabled;
            private Boolean inPlayOnly;
            private List<String> marketBettingTypes;
            private List<String> marketCountries;
            private List<String> marketTypeCodes;
            private String marketStartTime;
            private List<String> withOrders;

            public void setTextQuery(String textQuery)
            {
                this.textQuery = textQuery;
            }

            public void setExchangeIds(List<String> exchangeIds)
            {
                this.exchangeIds = exchangeIds;
            }

            public void setEventTypeIds(List<String> eventTypeIds)
            {
                this.eventTypeIds = eventTypeIds;
            }

            public void setEventTypeIds(String... types)
            {
                this.eventTypeIds = new ArrayList<>(Arrays.asList(types));
            }

            public void setEventIds(List<String> eventIds)
            {
                this.eventIds = eventIds;
            }

            public void setEventIds(String... ids)
            {
                this.eventIds = new ArrayList<>(Arrays.asList(ids));
            }

            public void setCompetitionIds(List<String> competitionIds)
            {
                this.competitionIds = competitionIds;
            }

            public void setMarketIds(List<String> marketIds)
            {
                this.marketIds = marketIds;
            }

            public void setVenues(List<String> venues)
            {
                this.venues = venues;
            }

            public void setBspOnly(boolean bspOnly)
            {
                this.bspOnly = bspOnly;
            }

            public void setTurnInPlayEnabled(boolean turnInPlayEnabled)
            {
                this.turnInPlayEnabled = turnInPlayEnabled;
            }

            public void setInPlayOnly(boolean inPlayOnly)
            {
                this.inPlayOnly = inPlayOnly;
            }

            public void setMarketBettingTypes(List<String> marketBettingTypes)
            {
                this.marketBettingTypes = marketBettingTypes;
            }

            public void setMarketCountries(List<String> marketCountries)
            {
                this.marketCountries = marketCountries;
            }

            public void setMarketTypeCodes(List<String> marketTypeCodes)
            {
                this.marketTypeCodes = marketTypeCodes;
            }

            public void setMarketTypeCodes(String... codes)
            {
                this.marketTypeCodes = new ArrayList<>(Arrays.asList(codes));
            }

            public void setMarketStartTime(String marketStartTime)
            {
                this.marketStartTime = marketStartTime;
            }

            public void setWithOrders(List<String> withOrders)
            {
                this.withOrders = withOrders;
            }

            public MarketFilter build()
            {
                return new MarketFilter(textQuery, exchangeIds, eventTypeIds, eventIds, competitionIds, marketIds, venues, bspOnly, turnInPlayEnabled, inPlayOnly, marketBettingTypes, marketCountries, marketTypeCodes, marketStartTime, withOrders);
            }
        }
    }
}