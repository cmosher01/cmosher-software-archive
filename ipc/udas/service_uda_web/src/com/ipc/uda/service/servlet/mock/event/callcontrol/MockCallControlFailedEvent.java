/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.event.callcontrol;

import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.UserData;
import com.ipc.va.cti.callControl.events.CallControlFailedEvent;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;

/**
 * @author mordarsd
 *
 */
public class MockCallControlFailedEvent extends MockCallControlEvent implements CallControlFailedEvent
{
    
    private DeviceID calledDeviceID;
    private DeviceID callingDeviceID;
    private DeviceID failingDeviceID;
    private DeviceID lastRedirectionDeviceID;
    
    
    
    public MockCallControlFailedEvent()
    {
        this(null, null, null, null);
    }
    
    

    /**
     * @param calledDeviceID
     * @param callingDeviceID
     * @param failingDeviceID
     * @param lastRedirectionDeviceID
     */
    public MockCallControlFailedEvent(DeviceID calledDeviceID, DeviceID callingDeviceID, DeviceID failingDeviceID,
            DeviceID lastRedirectionDeviceID)
    {
        this(null, null, null, calledDeviceID, callingDeviceID, failingDeviceID, lastRedirectionDeviceID);
    }

    /**
     * @param connectionID
     * @param eventCause
     * @param userData
     * @param calledDeviceID
     * @param callingDeviceID
     * @param failingDeviceID
     * @param lastRedirectionDeviceID
     */
    public MockCallControlFailedEvent(ConnectionID connectionID, EventCause eventCause, UserData userData,
            DeviceID calledDeviceID, DeviceID callingDeviceID, DeviceID failingDeviceID, DeviceID lastRedirectionDeviceID)
    {
        this(null, null, connectionID, eventCause, userData, calledDeviceID, callingDeviceID, failingDeviceID, lastRedirectionDeviceID);
    }

    /**
     * @param monitorCrossRefID
     * @param notifyToURL
     * @param connectionID
     * @param eventCause
     * @param userData
     * @param calledDeviceID
     * @param callingDeviceID
     * @param failingDeviceID
     * @param lastRedirectionDeviceID
     */
    public MockCallControlFailedEvent(MonitorCrossRefID monitorCrossRefID, String notifyToURL, ConnectionID connectionID,
            EventCause eventCause, UserData userData, DeviceID calledDeviceID, DeviceID callingDeviceID,
            DeviceID failingDeviceID, DeviceID lastRedirectionDeviceID)
    {
        super(monitorCrossRefID,notifyToURL,connectionID,eventCause,userData);
        this.calledDeviceID = calledDeviceID;
        this.callingDeviceID = callingDeviceID;
        this.failingDeviceID = failingDeviceID;
        this.lastRedirectionDeviceID = lastRedirectionDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlFailedEvent#getCalledDeviceID()
     */
    @Override
    public DeviceID getCalledDeviceID()
    {
        return this.calledDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlFailedEvent#getCallingDeviceID()
     */
    @Override
    public DeviceID getCallingDeviceID()
    {
        return this.callingDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlFailedEvent#getFailingDeviceID()
     */
    @Override
    public DeviceID getFailingDeviceID()
    {
        return this.failingDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlFailedEvent#getLastRedirectionDeviceID()
     */
    @Override
    public DeviceID getLastRedirectionDeviceID()
    {
        return this.lastRedirectionDeviceID;
    }

    /**
     * @param calledDeviceID the calledDeviceID to set
     */
    public void setCalledDeviceID(DeviceID calledDeviceID)
    {
        this.calledDeviceID = calledDeviceID;
    }

    /**
     * @param callingDeviceID the callingDeviceID to set
     */
    public void setCallingDeviceID(DeviceID callingDeviceID)
    {
        this.callingDeviceID = callingDeviceID;
    }

    /**
     * @param failingDeviceID the failingDeviceID to set
     */
    public void setFailingDeviceID(DeviceID failingDeviceID)
    {
        this.failingDeviceID = failingDeviceID;
    }

    /**
     * @param lastRedirectionDeviceID the lastRedirectionDeviceID to set
     */
    public void setLastRedirectionDeviceID(DeviceID lastRedirectionDeviceID)
    {
        this.lastRedirectionDeviceID = lastRedirectionDeviceID;
    }

    
    
    
}
