/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.event.physical;

import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;
import com.ipc.va.cti.physicalDevice.events.PhysicalDeviceMessageWaitingEvent;

/**
 * @author mordarsd
 *
 */
public class MockPhysicalDeviceMessageWaitingEvent extends MockPhysicalDeviceEvent implements PhysicalDeviceMessageWaitingEvent
{

    private boolean messageWaiting;
    
    
    public MockPhysicalDeviceMessageWaitingEvent()
    {
        this(false);
    }
    
    /**
     * @param messageWaiting
     */
    public MockPhysicalDeviceMessageWaitingEvent(boolean messageWaiting)
    {
        this(null, messageWaiting);
    }

    /**
     * @param deviceID
     * @param messageWaiting
     */
    public MockPhysicalDeviceMessageWaitingEvent(DeviceID deviceID, boolean messageWaiting)
    {
        this(null, null, deviceID, messageWaiting);
    }

    /**
     * @param monitorCrossRefID
     * @param notifyToURL
     * @param deviceID
     * @param messageWaiting
     */
    public MockPhysicalDeviceMessageWaitingEvent(MonitorCrossRefID monitorCrossRefID, String notifyToURL, DeviceID deviceID,
            boolean messageWaiting)
    {
        super(monitorCrossRefID,notifyToURL,deviceID);
        this.messageWaiting = messageWaiting;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceMessageWaitingEvent#isMessageWaiting()
     */
    @Override
    public boolean isMessageWaiting()
    {
        return this.messageWaiting;
    }

    /**
     * @param messageWaiting the messageWaiting to set
     */
    public void setMessageWaiting(boolean messageWaiting)
    {
        this.messageWaiting = messageWaiting;
    }

    
    
}
