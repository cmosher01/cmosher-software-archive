/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.event.physical;

import com.ipc.uda.service.servlet.mock.event.monitoring.MockMonitorEvent;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;
import com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent;

/**
 * @author mordarsd
 *
 */
public class MockPhysicalDeviceEvent extends MockMonitorEvent implements PhysicalDeviceEvent
{
    
    private DeviceID deviceID;

    
    public MockPhysicalDeviceEvent()
    {
        this(null);
    }
    
    /**
     * @param deviceID
     */
    public MockPhysicalDeviceEvent(DeviceID deviceID)
    {
        this(null, null, deviceID);
    }

    /**
     * @param monitorCrossRefID
     * @param notifyToURL
     * @param deviceID
     */
    public MockPhysicalDeviceEvent(MonitorCrossRefID monitorCrossRefID, String notifyToURL, DeviceID deviceID)
    {
        super(monitorCrossRefID,notifyToURL);
        this.deviceID = deviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent#getDeviceID()
     */
    @Override
    public DeviceID getDeviceID()
    {
        return this.deviceID;
    }

    /**
     * @param deviceID the deviceID to set
     */
    public void setDeviceID(DeviceID deviceID)
    {
        this.deviceID = deviceID;
    }

    
    
}
