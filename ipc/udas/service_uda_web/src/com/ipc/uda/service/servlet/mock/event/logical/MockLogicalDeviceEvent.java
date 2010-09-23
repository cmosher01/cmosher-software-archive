/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.event.logical;

import com.ipc.uda.service.servlet.mock.MockCtiEvent;
import com.ipc.uda.service.servlet.mock.event.monitoring.MockMonitorEvent;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.logicalDevice.AgentID;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;

/**
 * @author mordarsd
 *
 */
public class MockLogicalDeviceEvent extends MockMonitorEvent implements LogicalDeviceEvent, MockCtiEvent
{

    private AgentID agentID;
    private DeviceID deviceID;
    
    
    public MockLogicalDeviceEvent()
    {
        this(null, null);
    }
    
    /**
     * @param agentID
     * @param deviceID
     */
    public MockLogicalDeviceEvent(AgentID agentID, DeviceID deviceID)
    {
        this(null, null,agentID, deviceID);
    }
    
    

    /**
     * @param monitorCrossRefID
     * @param notifyToURL
     */
    public MockLogicalDeviceEvent(MonitorCrossRefID monitorCrossRefID, String notifyToURL,
                                  AgentID agentID, DeviceID deviceID)
    {
        super(monitorCrossRefID,notifyToURL);
        this.agentID = agentID;
        this.deviceID = deviceID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent#getAgentID()
     */
    @Override
    public AgentID getAgentID()
    {
        return this.agentID;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent#getDeviceID()
     */
    @Override
    public DeviceID getDeviceID()
    {
        return this.deviceID;
    }

    /**
     * @param agentID the agentID to set
     */
    public void setAgentID(AgentID agentID)
    {
        this.agentID = agentID;
    }

    /**
     * @param deviceID the deviceID to set
     */
    public void setDeviceID(DeviceID deviceID)
    {
        this.deviceID = deviceID;
    }

    @Override
    public void setProperty(String name, String value)
    {
        
        
    }
    
    
}
