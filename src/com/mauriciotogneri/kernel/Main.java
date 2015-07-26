package com.mauriciotogneri.kernel;

import com.mauriciotogneri.kernel.Constants.Log;
import com.mauriciotogneri.kernel.api.accounts.Login;
import com.mauriciotogneri.kernel.api.accounts.Login.LoginResponse;
import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.dependency.AppObjectProvider;
import com.mauriciotogneri.kernel.dependency.CustomObjectProvider;
import com.mauriciotogneri.kernel.models.Config;
import com.mauriciotogneri.kernel.models.Config.ConfigLogin;
import com.mauriciotogneri.kernel.models.Config.ConfigMonitor;
import com.mauriciotogneri.kernel.monitors.EventMonitor;
import com.mauriciotogneri.kernel.monitors.SessionMonitor;
import com.mauriciotogneri.kernel.utils.IoUtils;
import com.mauriciotogneri.kernel.utils.JsonUtils;

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

            SessionMonitor sessionMonitor = new SessionMonitor(HttpClient.getDefault(), session, configLogin.username, configLogin.password);
            sessionMonitor.start();

            for (ConfigMonitor monitor : monitors)
            {
                if (monitor.enabled)
                {
                    EventMonitor eventMonitor = new EventMonitor(HttpClient.getDefault(), session, monitor.sportType, monitor.inPlay, monitor.marketTypes);
                    eventMonitor.start();
                }
            }
        }
    }
}