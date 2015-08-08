package com.mauriciotogneri.betfair;

import com.mauriciotogneri.betfair.Constants.Log;
import com.mauriciotogneri.betfair.api.accounts.Login;
import com.mauriciotogneri.betfair.api.accounts.Login.LoginResponse;
import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.dependency.AppObjectProvider;
import com.mauriciotogneri.betfair.dependency.CustomObjectProvider;
import com.mauriciotogneri.betfair.models.Config;
import com.mauriciotogneri.betfair.models.Config.ConfigLogin;
import com.mauriciotogneri.betfair.models.Config.ConfigMonitor;
import com.mauriciotogneri.betfair.monitors.EventMonitor;
import com.mauriciotogneri.betfair.monitors.FundsMonitor;
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
        CustomObjectProvider customObjectProvider = new CustomObjectProvider(Log.ERROR_LOG_PATH, Log.PROFIT_LOG_PATH, Log.ACTIVITY_LOG_PATH, Log.FUNDS_LOG_PATH, Log.WALLET_LOG_PATH);
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

            SessionMonitor sessionMonitor = new SessionMonitor(HttpClient.getDefault(), session, configLogin.username, configLogin.password);
            sessionMonitor.start();

            FundsMonitor fundsMonitor = new FundsMonitor(HttpClient.getDefault(), session);
            fundsMonitor.start();

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
}