/*
 * Created on April 20, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.monitor;

import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItemDefault;

/**
 * Like <code>ChangePrinter</code>, but filters out unchanged items.
 * 
 * @author Chris Mosher
 */
public class ChangeFilterPrinter extends ChangePrinter implements ChangeListener
{
    /**
     * @see com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener#itemChanged(com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem)
     */
    public void itemChanged(final MonitorableItem item)
    {
        final int iItem = rItem.indexOf(item);
        if (iItem >= 0)
        {
            MonitorableItemDefault oldItem = (MonitorableItemDefault)rItem.get(iItem);
            MonitorableItemDefault newItem = (MonitorableItemDefault)item;
            if (newItem.isDeleted() && oldItem.isDeleted())
            {
                // this shouldn't happen?
                System.out.println("Ignoring notification of unchanged (deleted) item.");
            }
            else if (newItem.isDeleted())
            {
                super.itemChanged(item);
            }
            else if (newItem.getContents().equals(oldItem.getContents()))
            {
                System.out.println("Ignoring notification of changed item where there were no actual net changes.");
            }
            else
            {
                super.itemChanged(item);
            }
        }
        else
        {
            super.itemChanged(item);
        }
    }
}
