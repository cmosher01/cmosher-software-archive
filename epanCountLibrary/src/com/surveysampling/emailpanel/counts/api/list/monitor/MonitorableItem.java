/*
 * Created on April 18, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.monitor;

/**
 * An item that can be monitored by a <code>ChangeMonitor</code>.
 * 
 * @author Chris Mosher
 */
public interface MonitorableItem
{
    /**
     * Returns the serial number of the (latest) change to
     * this item.
     * @return date last modified
     */
    ChangeSerialNumber getLastChangeSerialNumber();

    /**
     * @param obj
     * @return returns <code>true</code> if this object refers to
     * the same item as the given object
     */
    boolean equals(Object obj);

    /**
     * @return a hash code for the item
     */
    int hashCode();

    /**
     * Returns if this item has been deleted.
     * @return </code>true</code> if this item has been deleted
     */
    boolean isDeleted();
}
