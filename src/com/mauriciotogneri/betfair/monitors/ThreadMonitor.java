package com.mauriciotogneri.betfair.monitors;

import com.mauriciotogneri.betfair.Constants.Execution;
import com.mauriciotogneri.betfair.logs.ErrorLog;
import com.mauriciotogneri.betfair.logs.ThreadLog;
import com.mauriciotogneri.betfair.utils.IoUtils;
import com.mauriciotogneri.betfair.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ThreadMonitor extends Thread
{
    private static final int WAITING_TIME = 60 * 1000; // one minute (in milliseconds)

    public ThreadMonitor()
    {
        super("THREAD MONITOR");
    }

    @Override
    public void run()
    {
        try
        {
            long startTime;
            long processTime;

            boolean continueRunning = true;

            while (continueRunning || isRunning())
            {
                startTime = System.currentTimeMillis();

                try
                {
                    List<Thread> threads = getThreads();
                    ThreadLog.log(threads);

                    continueRunning = !threads.isEmpty();
                }
                catch (Exception e)
                {
                    ErrorLog.log(e);
                }
                finally
                {
                    processTime = System.currentTimeMillis() - startTime;
                }

                threadSleep(WAITING_TIME - processTime);
            }
        }
        catch (Exception e)
        {
            ErrorLog.log(e);
        }
        finally
        {
            // TODO: SEND EMAIL TO NOTIFY THE BOT HAS FINISHED
        }
    }

    private List<Thread> getThreads()
    {
        List<Thread> result = new ArrayList<>();

        Set<Thread> threads = Thread.getAllStackTraces().keySet();

        for (Thread thread : threads)
        {
            if ((getId() != thread.getId()) && StringUtils.contains(thread.getName(), "MONITOR"))
            {
                result.add(thread);
            }
        }

        Collections.sort(result, new Comparator<Thread>()
        {
            @Override
            public int compare(Thread t1, Thread t2)
            {
                return (int) (t1.getId() - t2.getId());
            }
        });

        return result;
    }

    private boolean isRunning()
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