/*
 * Created on April 18, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException;

/**
 * Monitors a set for changes, and notifies any listeners.
 * 
 * @author Chris Mosher
 */
public final class ChangeMonitor
{
    private final MonitorableSet monitorable;
    private final int checkEveryMS;

    private boolean flagStop;
    private boolean flagForceCheck;

    private final Thread threadMonitor;

    private final Map mapListener = new HashMap();
    private final Collection rListenerRef = new ArrayList();



    /**
     * Creates a monitor for the given <code>MonitorableSet</code>.
     * @param monitorable
     * @param checkEverySeconds
     */
    public ChangeMonitor(final MonitorableSet monitorable, final int checkEverySeconds)
    {
        this.monitorable = monitorable;
        this.checkEveryMS = checkEverySeconds*1000;
        this.threadMonitor = createMonitorThread();
        this.threadMonitor.start();
    }



    private Thread createMonitorThread()
    {
        return new Thread(
            new Runnable()
            {
                public void run()
                {
                    try
                    {
                        monitorThreadRun();
                    }
                    catch (final Throwable e)
                    {
                        e.printStackTrace();
                    }
                }
            },
            this.getClass().getName());
    }



    /**
     * The <code>MonitorableSet</code> that was passed in
     * to the constructor.
     * @return <code>MonitorableSet</code>
     */
    public MonitorableSet getMonitorableSet()
    {
        return this.monitorable;
    }

    /**
     * Adds a listener to be notified of changes by this monitor.
     * @param listener
     * @return <code>true</code> if the list changed as a result of the addition
     * @throws IllegalStateException is <code>close</code> has already been called for this monitor
     */
    public synchronized boolean addChangeListener(final ChangeListener listener)
    {
        verifyMonitoring();
        if (mapListener.containsKey(listener))
        {
            return false;
        }

        ListenerRef refListener = new ListenerRef(listener);
        mapListener.put(listener,refListener);
        rListenerRef.add(refListener);

        return true;
    }

    /**
     * Removes the given listener from this monitor's list, if it's there.
     * @param listener
     * @return <code>true</code> if the listener was removed,
     * <code>false</code> if the listener wasn't in this monitor's list.
     * @throws IllegalStateException is <code>close</code> has already been called for this monitor
     */
    public synchronized boolean removeChangeListener(final ChangeListener listener)
    {
        verifyMonitoring();
        if (!mapListener.containsKey(listener))
        {
            return false;
        }

        ListenerRef refListener = (ListenerRef)mapListener.get(listener);
        rListenerRef.remove(refListener);
        mapListener.remove(listener);

        return true;
    }

    /**
     * Removes all listeners from this monitor's list, if any.
     * @return <code>true</code> if any listeners were removed,
     * <code>false</code> if there were no listeners in the first place
     * @throws IllegalStateException is <code>close</code> has already been called for this monitor
     */
    public synchronized boolean removeAllChangeListeners()
    {
        if (mapListener.size() == 0)
        {
            return false;
        }
        mapListener.clear();
        rListenerRef.clear();
        return true;
    }



    /**
     * Forces this monitor to check for changes immediately, instead
     * of waiting for the next normal periodic check.
     * @throws IllegalStateException is <code>close</code> has already been called for this monitor
     */
    public synchronized void forceCheckNow()
    {
        verifyMonitoring();
        this.flagForceCheck = true;
        notifyAll();
    }

    private synchronized void clearForceCheckNow()
    {
        this.flagForceCheck = false;
    }

    private void verifyMonitoring() throws IllegalStateException
    {
        if (this.flagStop)
        {
            throw new IllegalStateException("Monitor is not currently monitoring.");
        }
    }

    /**
     * Gets if this monitor is currently monitoring or not.
     * @return <code>true</code> if not currently monitoring
     */
    public synchronized boolean isStopped()
    {
        return this.flagStop;
    }

    /**
     * Calls <code>close</code>.
     * @see java.lang.Object#finalize()
     */
    public void finalize()
    {
        close();
    }

    /**
     * Releases resources held by this monitor. Removes
     * all listeners and stops the monitoring process.
     */
    public void close()
    {
        removeAllChangeListeners();

        synchronized (this)
        {
            if (this.flagStop)
            {
                return;
            }

            this.flagStop = true;
            notifyAll();
        }

        joinMonitorThread();
    }






    private void joinMonitorThread()
    {
        try
        {
            this.threadMonitor.join();
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }












    void monitorThreadRun()
    {
        waitUntilRunnable();
        while (!isStopped())
        {
            clearForceCheckNow();
            checkForChanges();
            waitUntilRunnable();
        }
    }



    /**
     * 
     */
    private void checkForChanges()
    {
        /*
         * Make local (and immutable) copy (while synchronized)
         * of the list of listeners, then we can do un-synchronized
         * access of our local copy.
         */
        final List rLocalListener = getCopyOfListeners();

        /*
         * Go through each registered listener, and see if we have
         * any changes for him.
         */
        for (final Iterator iListener = rLocalListener.iterator(); iListener.hasNext();)
        {
            ListenerRef listener = (ListenerRef)iListener.next();
            getChangesAndNotifyListener(listener);
        }
//        TODO try this instead, in Java 5.0:
//        for (ListenerRef listener : rLocalListener)
//        {
//            getChangesAndNotifyListener(listener);
//        }
    }

    private void getChangesAndNotifyListener(ListenerRef listener)
    {
        try
        {
            final Set rChangedItem = new HashSet();

            this.monitorable.getChangesSince(listener.getLastChangeSerialNumber(),rChangedItem);

            notifyListeners(listener,rChangedItem);
        }
        catch (final MonitoringException e)
        {
            listener.exception(e);
        }
    }



    /**
     * @return (immutable) copy of listener list
     */
    private synchronized List getCopyOfListeners()
    {
        return Collections.unmodifiableList(new ArrayList(this.rListenerRef));
    }



    /**
     * @param listener
     * @param rChangedItem
     */
    private void notifyListeners(final ListenerRef listener, final Collection rChangedItem)
    {
//        TODO in Java 5.0:
//        for (MonitorableItem item : rChangedItem)
//        {
//            listener.itemChanged(item);
//        }
        for (final Iterator iChangedItem = rChangedItem.iterator(); iChangedItem.hasNext();)
        {
            final MonitorableItem item = (MonitorableItem)iChangedItem.next();
            listener.itemChanged(item);
        }
    }



    private synchronized void waitUntilRunnable()
    {
        while (!this.flagForceCheck && !this.flagStop)
        {
            try
            {
                long t0 = System.currentTimeMillis();
                wait(this.checkEveryMS);
                if (t0+this.checkEveryMS <= System.currentTimeMillis())
                {
                    // we waited long enough, so do the periodic check
                    return;
                }
                // else, we only check if forced to, or if quitting
                // (that is, loop if it's a spurious wakeup)
            }
            catch (final InterruptedException e)
            {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }









    /**
     * A wrapper around a <code>ChangeListener</code> that keeps
     * track of the latest last-modified date of the changes
     * we notify the listener of.
     * 
     * @author Chris Mosher
     */
    private static final class ListenerRef
    {
        private final ChangeListener listener;
        private ChangeSerialNumber lastMod = ChangeSerialNumber.getSerialNumberLowerLimit();

        ListenerRef(final ChangeListener listener)
        {
            this.listener = listener;
        }

        ChangeSerialNumber getLastChangeSerialNumber()
        {
            return this.lastMod;
        }

        void itemChanged(final MonitorableItem item)
        {
            updateLatestLastModDate(item);
            this.listener.itemChanged(item);
        }

        void exception(final MonitoringException e)
        {
            this.listener.exception(e);
        }

        private void updateLatestLastModDate(final MonitorableItem item)
        {
            if (this.lastMod.compareTo(item.getLastChangeSerialNumber()) < 0)
            {
                this.lastMod = item.getLastChangeSerialNumber();
            }
        }
    }
}
