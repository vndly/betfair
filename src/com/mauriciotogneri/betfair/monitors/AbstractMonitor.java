package com.mauriciotogneri.betfair.monitors;

import com.mauriciotogneri.betfair.Constants.Execution;
import com.mauriciotogneri.betfair.api.base.HttpClient;
import com.mauriciotogneri.betfair.api.base.Session;
import com.mauriciotogneri.betfair.logs.ErrorLog;
import com.mauriciotogneri.betfair.utils.IoUtils;

import java.io.IOException;

public abstract class AbstractMonitor extends Thread
{
    protected final HttpClient httpClient;
    protected final Session session;

    public AbstractMonitor(HttpClient httpClient, Session session, String name)
    {
        super(name);

        this.httpClient = httpClient;
        this.session = session;
    }

    protected int getWaitTime()
    {
        return 0;
    }

    protected boolean onPreExecute() throws Exception
    {
        return true;
    }

    protected void onPostExecute(boolean executed) throws Exception
    {
    }

    protected abstract boolean execute() throws Exception;

    @Override
    public void run()
    {
        boolean executed = false;

        try
        {
            if (onPreExecute())
            {
                executed = true;

                int waitingTime = getWaitTime();
                boolean continueExecuting = true;

                long startTime;
                long processTime;

                while (continueExecuting)
                {
                    startTime = System.currentTimeMillis();

                    try
                    {
                        continueExecuting = execute();
                    }
                    catch (Exception e)
                    {
                        ErrorLog.log(e);
                    }
                    finally
                    {
                        processTime = System.currentTimeMillis() - startTime;
                    }

                    if (continueExecuting)
                    {
                        threadSleep(waitingTime - processTime);
                    }
                }
            }
        }
        catch (Exception e)
        {
            ErrorLog.log(e);
        }
        finally
        {
            try
            {
                onPostExecute(executed);
            }
            catch (Exception e)
            {
                // ignore
            }
        }
    }

    protected boolean isRunning()
    {
        try
        {
            return !IoUtils.isFileFilled(Execution.IS_RUNNING_FLAG);
        }
        catch (IOException e)
        {
            ErrorLog.log(e);
        }

        return true;
    }

    private void threadSleep(long milliseconds)
    {
        if (milliseconds > 0)
        {
            try
            {
                Thread.sleep(milliseconds);
            }
            catch (InterruptedException e)
            {
                // ignore
            }
        }
    }
}