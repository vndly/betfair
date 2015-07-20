package com.mauriciotogneri.kernel.monitors;

import com.mauriciotogneri.kernel.api.base.HttpClient;
import com.mauriciotogneri.kernel.api.base.Session;

public abstract class AbstractMonitor extends Thread
{
    protected final HttpClient httpClient;
    protected final Session session;

    public AbstractMonitor(HttpClient httpClient, Session session)
    {
        this.httpClient = httpClient;
        this.session = session;
    }

    protected int getWaitTime()
    {
        return 0;
    }

    protected void onException(Exception e)
    {
    }

    protected boolean onPreExecute() throws Exception
    {
        return true;
    }

    protected void onPostExecute()
    {
    }

    protected abstract boolean execute() throws Exception;

    @Override
    public void run()
    {
        try
        {
            if (onPreExecute())
            {
                int waitingTime = getWaitTime();
                boolean continueExecuting = true;

                while (continueExecuting)
                {
                    continueExecuting = execute();

                    if (continueExecuting && (waitingTime > 0))
                    {
                        threadSleep(waitingTime);
                    }
                }
            }
        }
        catch (Exception e)
        {
            // TODO

            onException(e);
        }
        finally
        {
            onPostExecute();
        }
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
                e.printStackTrace();
            }
        }
    }
}