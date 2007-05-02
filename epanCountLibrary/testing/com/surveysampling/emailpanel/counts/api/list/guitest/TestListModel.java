/*
 * Created on April 22, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.guitest;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem;
import com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException;

/**
 * Used for two purposes: 1. a <code>ListModel</code>,
 * which holds all data ("the model") for a <code>JList</code>; and,
 * 2. a <code>ChangeListener</code> that listens for changes
 * detected by a <code>ChangeMonitor</code>. This implements
 * a simple bridge between a <code>ChangeMonitor</code> and
 * a graphical list of the items being monitored.
 * 
 * @author Chris Mosher
 */
public class TestListModel extends AbstractListModel implements ListModel, ChangeListener
{
    private final List rItem = new ArrayList();



    /**
     * @see javax.swing.ListModel#getSize()
     */
    public synchronized int getSize()
    {
        return this.rItem.size();
    }

    /**
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public synchronized Object getElementAt(final int index)
    {
        return this.rItem.get(index);
    }

    /**
     * @see com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener#itemChanged(com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem)
     */
    public synchronized void itemChanged(final MonitorableItem item)
    {
        if (this.rItem.contains(item)) // if we already have the item
        {
            if (item.isDeleted())
            {
                // The item has been deleted, so remove it from our list (and fire listeners)
                deleteItem(item);
            }
            else
            {
                // The item has changed, so update it in our list (and fire listeners)
                updateItem(item);
            }
        }
        else // if we don't already have the item
        {
            if (item.isDeleted())
            {
                // Received notification of deletion of an item we don't have; ignore.
            }
            else
            {
                // It is a new item, so add it to our list (and fire listeners)
                insertItem(item);
            }
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



    /**
     * @param item
     */
    private void insertItem(final MonitorableItem item)
    {
        this.rItem.add(item);
        final int iItem = this.rItem.indexOf(item);
        fireIntervalAdded(this,iItem,iItem);
    }

    /**
     * @param item
     */
    private void updateItem(final MonitorableItem item)
    {
        final int iItem = this.rItem.indexOf(item);
        this.rItem.remove(item);
        this.rItem.add(iItem,item);
        fireContentsChanged(this,iItem,iItem);
    }

    /**
     * @param item
     */
    private void deleteItem(final MonitorableItem item)
    {
        final int iItem = this.rItem.indexOf(item);
        this.rItem.remove(item);
        fireIntervalRemoved(this,iItem,iItem);
    }
}
