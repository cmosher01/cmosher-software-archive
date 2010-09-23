/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.event.callcontrol;

import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.UserData;
import com.ipc.va.cti.callControl.events.CallControlEstablishedEvent;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;

/**
 * @author mordarsd
 *
 */
public class MockCallControlEstablishedEvent extends MockCallControlEvent implements CallControlEstablishedEvent
{

    private DeviceID answeringDeviceID;
    private DeviceID calledDeviceID;
    private DeviceID callingDeviceID;
    private DeviceID lastRedirectionDeviceID;
    
    
    public MockCallControlEstablishedEvent()
    {
        this(null, null, null, null);
    }
    
    /**
     * @param alertingDeviceID
     * @param calledDeviceID
     * @param callingDeviceID
     * @param lastRedirectionDeviceID
     */
    public MockCallControlEstablishedEvent(DeviceID alertingDeviceID, DeviceID calledDeviceID, DeviceID callingDeviceID,
            DeviceID lastRedirectionDeviceID)
    {
        this(null, null, null, alertingDeviceID, calledDeviceID, callingDeviceID, lastRedirectionDeviceID);
    }

    /**
     * @param connectionID
     * @param eventCause
     * @param userData
     * @param alertingDeviceID
     * @param calledDeviceID
     * @param callingDeviceID
     * @param lastRedirectionDeviceID
     */
    public MockCallControlEstablishedEvent(ConnectionID connectionID, EventCause eventCause, UserData userData,
            DeviceID alertingDeviceID, DeviceID calledDeviceID, DeviceID callingDeviceID, DeviceID lastRedirectionDeviceID)
    {
        this(null, null, connectionID, eventCause, userData, alertingDeviceID, 
             calledDeviceID, callingDeviceID, lastRedirectionDeviceID);
    }

    /**
     * @param monitorCrossRefID
     * @param notifyToURL
     * @param connectionID
     * @param eventCause
     * @param userData
     * @param answeringDeviceID
     * @param calledDeviceID
     * @param callingDeviceID
     * @param lastRedirectionDeviceID
     */
    public MockCallControlEstablishedEvent(MonitorCrossRefID monitorCrossRefID, String notifyToURL, ConnectionID connectionID,
            EventCause eventCause, UserData userData, DeviceID answeringDeviceID, DeviceID calledDeviceID,
            DeviceID callingDeviceID, DeviceID lastRedirectionDeviceID)
    {
        super(monitorCrossRefID,notifyToURL,connectionID,eventCause,userData);
        this.answeringDeviceID = answeringDeviceID;
        this.calledDeviceID = calledDeviceID;
        this.callingDeviceID = callingDeviceID;
        this.lastRedirectionDeviceID = lastRedirectionDeviceID;
    }
    
    /**
     * @param answeringDeviceID the answeringDeviceID to set
     */
    public void setAnsweringDeviceID(DeviceID answeringDeviceID)
    {
        this.answeringDeviceID = answeringDeviceID;
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
     * @param lastRedirectionDeviceID the lastRedirectionDeviceID to set
     */
    public void setLastRedirectionDeviceID(DeviceID lastRedirectionDeviceID)
    {
        this.lastRedirectionDeviceID = lastRedirectionDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlEstablishedEvent#getAnsweringDeviceID()
     */
    @Override
    public DeviceID getAnsweringDeviceID()
    {
        return this.answeringDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlEstablishedEvent#getCalledDeviceID()
     */
    @Override
    public DeviceID getCalledDeviceID()
    {
        return this.calledDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlEstablishedEvent#getCallingDeviceID()
     */
    @Override
    public DeviceID getCallingDeviceID()
    {
        return this.callingDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlEstablishedEvent#getLastRedirectionDeviceID()
     */
    @Override
    public DeviceID getLastRedirectionDeviceID()
    {
        return this.lastRedirectionDeviceID;
    }

}
