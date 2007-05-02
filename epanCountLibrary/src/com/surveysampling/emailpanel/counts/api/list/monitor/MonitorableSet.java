/*
 * Created on April 18, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.monitor;

import java.util.Set;

import com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException;

/**
 * A set that can be monitored by a <code>ChangeMonitor</code>. Each element
 * in the set must be a <code>MonitorableItem</code>.
 * 
 * @author Chris Mosher
 */
public interface MonitorableSet
{
    /**
     * Retrieves the set of changed items in this set,
     * including changes only newer than the given serial number.
     * @param lastChangeSerialNumber
     * @param setOfChangedItems
     * @throws MonitoringException
     */
    void getChangesSince(ChangeSerialNumber lastChangeSerialNumber, Set setOfChangedItems)
        throws MonitoringException;
}
