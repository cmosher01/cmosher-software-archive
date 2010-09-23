/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.event.callcontrol;

import com.ipc.uda.service.servlet.mock.MockCtiEvent;
import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.UserData;
import com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;

/**
 * @author mordarsd
 *
 */
public class MockCallControlConnectionClearedEvent extends MockCallControlEvent 
    implements CallControlConnectionClearedEvent, MockCtiEvent
{

    private DeviceID releasingDeviceID;
    
    
    public MockCallControlConnectionClearedEvent()
    {
        this(null);
    }
    
    
    /**
     * @param releasingDeviceID
     */
    public MockCallControlConnectionClearedEvent(DeviceID releasingDeviceID)
    {
        this(null, null, null, releasingDeviceID);
    }

    /**
     * @param connectionID
     * @param eventCause
     * @param userData
     * @param releasingDeviceID
     */
    public MockCallControlConnectionClearedEvent(ConnectionID connectionID, EventCause eventCause, UserData userData,
            DeviceID releasingDeviceID)
    {
        this(null, null, connectionID, eventCause, userData, releasingDeviceID);
    }

    /**
     * @param monitorCrossRefID
     * @param notifyToURL
     * @param connectionID
     * @param eventCause
     * @param userData
     * @param releasingDeviceID
     */
    public MockCallControlConnectionClearedEvent(MonitorCrossRefID monitorCrossRefID, String notifyToURL,
            ConnectionID connectionID, EventCause eventCause, UserData userData, DeviceID releasingDeviceID)
    {
        super(monitorCrossRefID,notifyToURL,connectionID,eventCause,userData);
        this.releasingDeviceID = releasingDeviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent#getReleasingDeviceID()
     */
    @Override
    public DeviceID getReleasingDeviceID()
    {
        return this.releasingDeviceID;
    }

    /**
     * @param releasingDeviceID the releasingDeviceID to set
     */
    public void setReleasingDeviceID(DeviceID releasingDeviceID)
    {
        this.releasingDeviceID = releasingDeviceID;
    }

    @Override
    public void setProperty(String name, String value)
    {
        
        
    }
    
    
}
