package com.mauriciotogneri.kernel.monitors;

import com.mauriciotogneri.kernel.api.accounts.KeepAlive;
import com.mauriciotogneri.kernel.api.accounts.Login;
import com.mauriciotogneri.kernel.api.accounts.Login.LoginResponse;
import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;
import com.mauriciotogneri.kernel.logs.ErrorLog;

public class SessionMonitor extends AbstractMonitor
{
    private final String username;
    private final String password;
    private final Session session;
    private final KeepAlive keepAlive;

    private static final int WAITING_TIME = 60 * 1000; // one minute (in milliseconds)

    public SessionMonitor(HttpClient httpClient, Session session, String username, String password)
    {
        super(httpClient, session);

        this.session = session;
        this.username = username;
        this.password = password;
        this.keepAlive = new KeepAlive(httpClient);
    }

    @Override
    protected int getWaitTime()
    {
        return WAITING_TIME;
    }

    @Override
    protected boolean execute() throws Exception
    {
        boolean loop = true;

        LoginResponse keepAliveResponse = keepAlive.execute(session.appKey, session.getSessionToken());

        if (keepAliveResponse.isValid())
        {
            if (!session.getSessionToken().equals(keepAliveResponse.token))
            {
                session.setSessionToken(keepAliveResponse.token);
            }
        }
        else
        {
            ErrorLog.log("KEEP ALIVE FAILED");

            Login login = new Login(httpClient);
            LoginResponse loginResponse = login.execute(username, password, session.appKey);

            if (loginResponse.isValid())
            {
                session.setSessionToken(loginResponse.token);
            }
            else
            {
                ErrorLog.log("LOGIN FAILED");

                loop = false;
            }
        }

        return loop;
    }
}