package com.mauriciotogneri.betfair;

import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.api.accounts.Login;
import com.mauriciotogneri.betfair.api.accounts.Login.LoginResponse;
import com.mauriciotogneri.betfair.api.base.Enums.Side;
import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.api.base.Types.CancelExecutionReport;
import com.mauriciotogneri.betfair.api.base.Types.PlaceExecutionReport;
import com.mauriciotogneri.betfair.api.betting.CancelOrders;
import com.mauriciotogneri.betfair.api.betting.PlaceOrders;
import com.mauriciotogneri.betfair.dependency.AppObjectProvider;
import com.mauriciotogneri.betfair.dependency.CustomObjectProvider;
import com.mauriciotogneri.betfair.models.Bet;
import com.mauriciotogneri.betfair.models.BetInstruction;
import com.mauriciotogneri.betfair.models.Config;
import com.mauriciotogneri.betfair.models.Config.ConfigLogin;
import com.mauriciotogneri.betfair.models.Config.ConfigMonitor;
import com.mauriciotogneri.betfair.monitors.EventMonitor;
import com.mauriciotogneri.betfair.monitors.SessionMonitor;
import com.mauriciotogneri.betfair.utils.IoUtils;
import com.mauriciotogneri.betfair.utils.JsonUtils;

import java.io.IOException;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Main main = new Main();
        main.init(args[0]);
    }

    private void init(String configFilePath) throws IOException
    {
        CustomObjectProvider customObjectProvider = new CustomObjectProvider(Log.ERROR_LOG_PATH);
        AppObjectProvider.init(customObjectProvider);

        Config config = JsonUtils.fromJson(IoUtils.readFile(configFilePath), Config.class);

        run(config.login, config.monitors);
    }

    private void run(ConfigLogin configLogin, List<ConfigMonitor> monitors) throws IOException
    {
        Login login = new Login(HttpClient.getDefault());
        LoginResponse loginResponse = login.execute(configLogin.username, configLogin.password, configLogin.appKey);

        if (loginResponse.isValid())
        {
            Session session = new Session(configLogin.appKey, loginResponse.token);

            //testBack(session);
            //testLay(session);

            SessionMonitor sessionMonitor = new SessionMonitor(HttpClient.getDefault(), session, configLogin.username, configLogin.password);
            sessionMonitor.start();

            for (ConfigMonitor monitor : monitors)
            {
                if (monitor.enabled)
                {
                    EventMonitor eventMonitor = new EventMonitor(HttpClient.getDefault(), session, monitor.eventType, monitor.inPlay, monitor.marketTypes);
                    eventMonitor.start();
                }
            }
        }
    }

    private void testBack(Session session) throws IOException
    {
        String marketId = "1.119607159x";
        long selectionId = 1221386;
        Side side = Side.BACK;
        double price = 1.74;
        double stake = 2;

        BetInstruction betInstruction = new BetInstruction(marketId, selectionId, side, price, stake);

        PlaceOrders placeOrders = PlaceOrders.getRequest(HttpClient.getDefault(), session, betInstruction);
        PlaceExecutionReport placeExecutionReport = placeOrders.execute();

        String json = JsonUtils.toJson(placeExecutionReport);
        System.out.print(json);
    }

    private void testLay(Session session) throws IOException
    {
        String marketId = "1.119607159";
        long selectionId = 1221386;
        Side side = Side.LAY;
        double price = 1.73;
        double stake = 2.01;

        BetInstruction betInstruction = new BetInstruction(marketId, selectionId, side, price, stake);

        PlaceOrders placeOrders = PlaceOrders.getRequest(HttpClient.getDefault(), session, betInstruction);
        PlaceExecutionReport placeExecutionReport = placeOrders.execute();

        System.out.print(JsonUtils.toJson(placeExecutionReport));

        if (placeExecutionReport.isValid())
        {
            System.out.print("PLACED");

            Bet bet = placeExecutionReport.getBet(betInstruction);
            System.out.print(JsonUtils.toJson(bet));

            CancelOrders cancelOrders = CancelOrders.getRequest(HttpClient.getDefault(), session, bet);
            CancelExecutionReport cancelExecutionReport = cancelOrders.execute();

            System.out.print(JsonUtils.toJson(cancelExecutionReport));

            if (cancelExecutionReport.isValid())
            {
                System.out.print("CANCELLED");
            }
            else
            {
                System.out.print("NOT CANCELLED");
            }
        }
        else
        {
            System.out.print("NOT PLACED");
        }

        System.out.print("FINISHED");
    }
}