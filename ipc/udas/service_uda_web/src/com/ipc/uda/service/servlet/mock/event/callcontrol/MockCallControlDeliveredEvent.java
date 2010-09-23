/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.event.callcontrol;

import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.UserData;
import com.ipc.va.cti.callControl.events.CallControlDeliveredEvent;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;

/**
 * @author mordarsd
 *
 */
public class MockCallControlDeliveredEvent extends MockCallControlEvent implements CallControlDeliveredEvent
{

    private DeviceID alertingDeviceID;
    private DeviceID calledDeviceID;
    private DeviceID callingDeviceID;
    private DeviceID lastRedirectionDeviceID;
    
    
    public MockCallControlDeliveredEvent()
    {
        this(null, null, null, null);
    }
    
    /**
     * @param alertingDeviceID
     * @param calledDeviceID
     * @param callingDeviceID
     * @param lastRedirectionDeviceID
     */
    public MockCallControlDeliveredEvent(DeviceID alertingDeviceID, DeviceID calledDeviceID, DeviceID callingDeviceID,
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
    public MockCallControlDeliveredEvent(ConnectionID connectionID, EventCause eventCause, UserData userData,
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
     * @param alertingDeviceID
     * @param calledDeviceID
     * @param callingDeviceID
     * @param lastRedirectionDeviceID
     */
    public MockCallControlDeliveredEvent(MonitorCrossRefID monitorCrossRefID, String notifyToURL, ConnectionID connectionID,
            EventCause eventCause, UserData userData, DeviceID alertingDeviceID, DeviceID calledDeviceID,
            DeviceID callingDeviceID, DeviceID lastRedirectionDeviceID)
    {
        super(monitorCrossRefID,notifyToURL,connectionID,eventCause,userData);
        this.alertingDeviceID = alertingDeviceID;
        this.calledDeviceID = calledDeviceID;
        this.callingDeviceID = callingDeviceID;
        this.lastRedirectionDeviceID = lastRedirectionDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlDeliveredEvent#getAlertingDeviceID()
     */
    @Override
    public DeviceID getAlertingDeviceID()
    {
        return this.alertingDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlDeliveredEvent#getCalledDeviceID()
     */
    @Override
    public DeviceID getCalledDeviceID()
    {
        return this.calledDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlDeliveredEvent#getCallingDeviceID()
     */
    @Override
    public DeviceID getCallingDeviceID()
    {
        return this.callingDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlDeliveredEvent#getLastRedirectionDeviceID()
     */
    @Override
    public DeviceID getLastRedirectionDeviceID()
    {
        return this.lastRedirectionDeviceID;
    }

    /**
     * @param alertingDeviceID the alertingDeviceID to set
     */
    public void setAlertingDeviceID(DeviceID alertingDeviceID)
    {
        this.alertingDeviceID = alertingDeviceID;
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

    
    
    
}
