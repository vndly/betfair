package com.mauriciotogneri.kernel;

import com.mauriciotogneri.kernel.api.accounts.Login;
import com.mauriciotogneri.kernel.api.accounts.Login.LoginResponse;
import com.mauriciotogneri.kernel.api.base.Enums.EventTypeEnum;
import com.mauriciotogneri.kernel.api.base.Enums.MarketTypeEnum;
import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.dependency.AppObjectProvider;
import com.mauriciotogneri.kernel.dependency.CustomObjectProvider;
import com.mauriciotogneri.kernel.monitors.EventMonitor;
import com.mauriciotogneri.kernel.monitors.SessionMonitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Main main = new Main();
        main.init(args[0]);
    }

    private void init(String configFilePath) throws IOException
    {
        InputStream input = new FileInputStream(configFilePath);
        Properties properties = new Properties();
        properties.load(input);
        input.close();

        CustomObjectProvider customObjectProvider = new CustomObjectProvider("logs/error.log");
        AppObjectProvider.init(customObjectProvider);

        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String appKey = properties.getProperty("appkey");

        run(username, password, appKey);
    }

    private void run(String username, String password, String appKey) throws IOException
    {
        Login login = new Login(HttpClient.getDefault());
        LoginResponse loginResponse = login.execute(username, password, appKey);

        Session session = new Session(appKey, loginResponse.token);

        String[] soccerMarkets = new String[1];
        soccerMarkets[0] = MarketTypeEnum.OVER_UNDER_15.toString();

        EventMonitor eventMonitorSoccer = new EventMonitor(HttpClient.getDefault(), session, EventTypeEnum.SOCCER.toString(), true, soccerMarkets);
        eventMonitorSoccer.start();

        //EventMonitor eventMonitorTennis = new EventMonitor(HttpClient.getDefault(), session, EventTypeEnum.TENNIS.toString(), true);
        //eventMonitorTennis.start();

        //EventMonitor eventMonitorHorse = new EventMonitor(HttpClient.getDefault(), session, EventTypeEnum.HORSE_RACING.toString(), false);
        //eventMonitorHorse.start();

        SessionMonitor sessionMonitor = new SessionMonitor(HttpClient.getDefault(), session, username, password);
        sessionMonitor.start();
    }
}