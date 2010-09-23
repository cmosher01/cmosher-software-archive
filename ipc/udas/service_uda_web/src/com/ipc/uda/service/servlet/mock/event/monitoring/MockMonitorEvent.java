/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.event.monitoring;

import com.ipc.va.cti.monitoring.MonitorCrossRefID;
import com.ipc.va.cti.monitoring.events.MonitorEvent;

/**
 * @author mordarsd
 *
 */
public class MockMonitorEvent implements MonitorEvent
{

    private MonitorCrossRefID monitorCrossRefID;
    private String notifyToURL;
    
    
    public MockMonitorEvent()
    {
        this(null, null);
    }
    
    /**
     * @param monitorCrossRefID
     * @param notifyToURL
     */
    public MockMonitorEvent(MonitorCrossRefID monitorCrossRefID, String notifyToURL)
    {
        this.monitorCrossRefID = monitorCrossRefID;
        this.notifyToURL = notifyToURL;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.monitoring.events.MonitorEvent#getMonitorCrossRefID()
     */
    @Override
    public MonitorCrossRefID getMonitorCrossRefID()
    {
        return this.monitorCrossRefID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.monitoring.events.MonitorEvent#getNotifyToURL()
     */
    @Override
    public String getNotifyToURL()
    {
        return this.notifyToURL;
    }
    
    public void setNotifyToURL(String notifyToUrl)
    {
        this.notifyToURL = notifyToUrl;
    }

    /**
     * @param monitorCrossRefID the monitorCrossRefID to set
     */
    public void setMonitorCrossRefID(MonitorCrossRefID monitorCrossRefID)
    {
        this.monitorCrossRefID = monitorCrossRefID;
    }
    
    

}
