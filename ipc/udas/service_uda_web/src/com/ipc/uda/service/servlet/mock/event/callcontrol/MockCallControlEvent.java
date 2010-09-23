/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.event.callcontrol;

import com.ipc.uda.service.servlet.mock.event.monitoring.MockMonitorEvent;
import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.UserData;
import com.ipc.va.cti.callControl.events.CallControlEvent;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;

/**
 * @author mordarsd
 *
 */
public class MockCallControlEvent extends MockMonitorEvent implements CallControlEvent
{
    
    private ConnectionID connectionID;
    private EventCause eventCause;
    private UserData userData;
    
    
    public MockCallControlEvent()
    {
        this(null, null, null);
    }
    
    
    /**
     * @param connectionID
     * @param eventCause
     * @param userData
     */
    public MockCallControlEvent(ConnectionID connectionID, EventCause eventCause, UserData userData)
    {
        this(null, null, connectionID, eventCause, userData);
    }

    /**
     * @param monitorCrossRefID
     * @param notifyToURL
     * @param connectionID
     * @param eventCause
     * @param userData
     */
    public MockCallControlEvent(MonitorCrossRefID monitorCrossRefID, String notifyToURL, ConnectionID connectionID,
            EventCause eventCause, UserData userData)
    {
        super(monitorCrossRefID,notifyToURL);
        this.connectionID = connectionID;
        this.eventCause = eventCause;
        this.userData = userData;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlEvent#getConnectionID()
     */
    @Override
    public ConnectionID getConnectionID()
    {
        return this.connectionID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlEvent#getEventCause()
     */
    @Override
    public EventCause getEventCause()
    {
        return this.eventCause;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlEvent#getUserData()
     */
    @Override
    public UserData getUserData()
    {
        return this.userData;
    }

    /**
     * @param connectionID the connectionID to set
     */
    public void setConnectionID(ConnectionID connectionID)
    {
        this.connectionID = connectionID;
    }

    /**
     * @param eventCause the eventCause to set
     */
    public void setEventCause(EventCause eventCause)
    {
        this.eventCause = eventCause;
    }

    /**
     * @param userData the userData to set
     */
    public void setUserData(UserData userData)
    {
        this.userData = userData;
    }

    
    
}
