package com.mauriciotogneri.betfair.strategies;

import com.mauriciotogneri.betfair.Constants;
import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.api.base.Enums.ExecutionReportStatus;
import com.mauriciotogneri.betfair.api.base.Enums.InstructionReportStatus;
import com.mauriciotogneri.betfair.api.base.Enums.OrderType;
import com.mauriciotogneri.betfair.api.base.Enums.PersistenceType;
import com.mauriciotogneri.betfair.api.base.Enums.Side;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.api.base.Types.CancelExecutionReport;
import com.mauriciotogneri.betfair.api.base.Types.LimitOrder;
import com.mauriciotogneri.betfair.api.base.Types.PlaceExecutionReport;
import com.mauriciotogneri.betfair.api.base.Types.PlaceInstruction;
import com.mauriciotogneri.betfair.api.base.Types.PlaceInstructionReport;
import com.mauriciotogneri.betfair.csv.CsvFile;
import com.mauriciotogneri.betfair.csv.CsvLine;
import com.mauriciotogneri.betfair.models.Bet;
import com.mauriciotogneri.betfair.models.BetInstruction;
import com.mauriciotogneri.betfair.models.Selection;
import com.mauriciotogneri.betfair.models.Tick;
import com.mauriciotogneri.betfair.utils.NumberUtils;
import com.mauriciotogneri.betfair.utils.TimeUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class StrategySoccerOverUnder15 extends Strategy
{
    private final String marketId;
    private State state = State.STARTED;

    private final Session session;

    private Bet initialBet = null;
    private Bet counterBet = null;

    private final CsvFile logPrice;
    private final CsvFile logActions;

    private static final int AFTER_10_MINUTES_PLAY = 1000 * 60 * 10; // 10 minutes of play (00:10:00)
    private static final int END_FIRST_HALF = 1000 * 60 * 45; // end of first half (00:45:00)
    private static final int END_HALF_TIME = 1000 * 60 * 60; // end of first half (01:00:00)
    private static final int END_SECOND_HALF = 1000 * 60 * 110; // end of second half (01:50:00)

    private static final double DEFAULT_STAKE = 2;
    private static final double MIN_BACK_PRICE = 1.1;
    private static final double MIN_LAY_PRICE = 1.02;

    private enum State
    {
        STARTED, BACKED, FINISHED
    }

    public StrategySoccerOverUnder15(Session session, String marketId, List<Long> selections, String logFolderPath) throws IOException
    {
        this.session = session;
        this.marketId = marketId;

        this.logPrice = new CsvFile(logFolderPath + Log.PRICES_LOG_FILE);
        this.logActions = new CsvFile(logFolderPath + Constants.Log.ACTIONS_LOG_FILE);

        initLogPrice(logPrice, selections);
    }

    private void initLogPrice(CsvFile logPrice, List<Long> selections) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.separator();

        for (long selectionId : selections)
        {
            csvLine.append(selectionId + "-back");
            csvLine.append(selectionId + "-lay");
        }

        logPrice.write(csvLine);
    }

    @Override
    public void onClose(long timestamp, boolean executed) throws Exception
    {
        if (initialBet != null)
        {
            if (counterBet != null)
            {
                double ifBack = initialBet.ifWin() - counterBet.ifLose();
                double ifLay = counterBet.ifWin() - initialBet.ifLose();

                logProfit(timestamp, (ifBack + ifLay) / 2);
            }
            else
            {
                if (isMatchFinished(timestamp))
                {
                    logProfit(timestamp, -initialBet.ifLose());
                }
                else
                {
                    logProfit(timestamp, initialBet.ifWin());
                }
            }
        }
    }

    @Override
    public void process(Tick tick) throws Exception
    {
        processAction(tick);
        logPrice(tick);
    }

    private void processAction(Tick tick) throws IOException
    {
        switch (state)
        {
            case STARTED:
                processStartedState(tick);
                break;

            case BACKED:
                processBackedState(tick);
                break;
        }
    }

    private void processStartedState(Tick tick) throws IOException
    {
        if (tick.allBackAvailable())
        {
            Selection selection = tick.getLowestBack();

            if (makeBackBet(tick, selection))
            {
                BetInstruction betInstruction = new BetInstruction(marketId, selection.id, Side.BACK, selection.back, DEFAULT_STAKE);

                PlaceExecutionReport placeExecutionReport = executeBet(betInstruction);

                if (placeExecutionReport.isValid())
                {
                    Bet bet = placeExecutionReport.getBet(betInstruction);

                    if (bet.isMatched)
                    {
                        initialBet = bet;

                        logBet(tick.timestamp, initialBet);

                        state = State.BACKED;
                    }
                    else
                    {
                        cancelBet(bet);

                        // TODO: check if the bet was cancelled
                    }
                }
            }
        }
    }

    private boolean makeBackBet(Tick tick, Selection selection)
    {
        return ((selection.back >= MIN_BACK_PRICE) && (selection.index == 1) && (!isFirstHalfFinished(tick.timestamp)) && isAfter10MinutesPlay(tick.timestamp));
    }

    private void processBackedState(Tick tick) throws IOException
    {
        double layPrice = tick.getLayPrice(initialBet.selectionId);

        if (isFirstHalfFinished(tick.timestamp) || (layPrice < MIN_LAY_PRICE))
        {
            if ((layPrice != 0) && (layPrice <= initialBet.price))
            {
                double counterStake = NumberUtils.round((DEFAULT_STAKE * initialBet.price) / layPrice, 2);

                BetInstruction betInstruction = new BetInstruction(marketId, initialBet.selectionId, Side.LAY, layPrice, counterStake);

                PlaceExecutionReport placeExecutionReport = executeBet(betInstruction);

                if (placeExecutionReport.isValid())
                {
                    Bet bet = placeExecutionReport.getBet(betInstruction);

                    if (bet.isMatched)
                    {
                        counterBet = bet;

                        logBet(tick.timestamp, counterBet);

                        state = State.FINISHED;
                    }
                    else
                    {
                        cancelBet(bet);

                        // TODO: check if the bet was cancelled
                    }
                }
            }
        }
    }

    private PlaceExecutionReport executeBet(BetInstruction betInstruction) throws IOException
    {
        //PlaceOrders placeOrders = PlaceOrders.getRequest(HttpClient.getDefault(), session, betInstruction);

        //return placeOrders.execute();

        LimitOrder limitOrder = new LimitOrder(betInstruction.stake, betInstruction.price, PersistenceType.PERSIST);

        PlaceInstructionReport placeInstructionReport = new PlaceInstructionReport();
        placeInstructionReport.status = InstructionReportStatus.SUCCESS;
        placeInstructionReport.instruction = new PlaceInstruction(OrderType.LIMIT, betInstruction.selectionId, betInstruction.side, limitOrder);
        placeInstructionReport.betId = UUID.randomUUID().toString();
        placeInstructionReport.placedDate = TimeUtils.getTimestamp();
        placeInstructionReport.averagePriceMatched = betInstruction.price;
        placeInstructionReport.sizeMatched = betInstruction.stake;

        PlaceExecutionReport placeExecutionReport = new PlaceExecutionReport();
        placeExecutionReport.customerRef = betInstruction.getRef();
        placeExecutionReport.status = ExecutionReportStatus.SUCCESS;
        placeExecutionReport.marketId = betInstruction.marketId;
        placeExecutionReport.instructionReports.add(placeInstructionReport);

        return placeExecutionReport;
    }

    private CancelExecutionReport cancelBet(Bet bet) throws IOException
    {
        //CancelOrders cancelOrders = CancelOrders.getRequest(HttpClient.getDefault(), session, bet);

        //return cancelOrders.execute();

        return null;
    }

    private boolean isAfter10MinutesPlay(long timestamp)
    {
        return timestamp > AFTER_10_MINUTES_PLAY;
    }

    private boolean isFirstHalfFinished(long timestamp)
    {
        return timestamp > END_FIRST_HALF;
    }

    private boolean isMatchFinished(long timestamp)
    {
        return timestamp > END_SECOND_HALF;
    }

    private void logBet(long timestamp, Bet bet) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(timestamp);
        csvLine.append("BET PLACED: " + bet.toString());
        logActions.write(csvLine);
    }

    private void logProfit(long timestamp, double profit) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(timestamp);
        csvLine.append("PROFIT: " + NumberUtils.round(profit, 2));
        logActions.write(csvLine);
    }

    private void logPrice(Tick tick) throws IOException
    {
        CsvLine csvLine = new CsvLine();
        csvLine.appendTimestamp(tick.timestamp);

        for (Selection selection : tick.selections)
        {
            csvLine.append(selection.back);
            csvLine.append(selection.lay);
        }

        logPrice.write(csvLine);
    }
}