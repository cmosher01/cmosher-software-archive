package com.surveysampling.mosher.selfrun;

public class SelfRun
{
    private boolean m_stop = false;

    private final Thread m_thread = new Thread(
        new Runnable()
        {
            public void run()
            {
                doRun();
            }
        });

    public SelfRun()
    {
        m_thread.start();
    }

    public void doRun() // should be private, but public prevents Eclipse warning
    {
        while (!isStopPending())
        {
            System.out.println("running");
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void requestStop()
    {
        m_stop = true;
        m_thread.interrupt();
    }

    public synchronized boolean isStopPending()
    {
        return m_stop;
    }

    public boolean isAlive()
    {
        return m_thread.isAlive();
    }
};
