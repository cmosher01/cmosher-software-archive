/*
 * Created on April 18, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.monitor;

import com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException;

/**
 * Listens for changes in a <code>MonitorableSet</code>, as
 * reported by a <code>ChangeMonitor</code>.
 * 
 * @author Chris Mosher
 */
public interface ChangeListener
{
    /**
     * The given <code>MonitorableItem</code> has changed.
     * Call the item's <code>isDeleted</code> method to
     * see if the item has been deleted.
     * @param item
     */
    void itemChanged(MonitorableItem item);

    /**
     * An exception occurred while trying to retrieve the
     * set of changes from the <code>MonitorableSet</code>.
     * @param e
     */
    void exception(MonitoringException e);
}
