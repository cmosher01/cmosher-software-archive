/*
 * Created on April 20, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.monitor;

import java.util.ArrayList;
import java.util.List;

import com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException;


/**
 * A simple implementation of <code>ChangeListener</code>
 * that just prints messages to standard output.
 * 
 * @author Chris Mosher
 */
public class ChangePrinter implements ChangeListener
{
    /**
     * Set of items we currently have.
     */
    protected final List rItem = new ArrayList();

    /**
     * @see com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener#itemChanged(com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem)
     */
    public void itemChanged(final MonitorableItem item)
    {
        final boolean had = rItem.contains(item);
        if (had)
        {
            rItem.remove(item);
        }

        if (item.isDeleted())
        {
            if (had)
            {
                System.out.println("Removed item: "+item);
            }
            else
            {
                System.out.println("Received notification of deletion of item we don't have: "+item);
            }
        }
        else
        {
            if (had)
            {
                System.out.println("Item changed: "+item);
            }
            else
            {
                System.out.println("Added item: "+item);
            }
            rItem.add(item);
        }
    }

    /**
     * @see com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener#exception(com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException)
     */
    public void exception(final MonitoringException e)
    {
        System.err.println("exception:");
        e.printStackTrace();
    }
    
}
