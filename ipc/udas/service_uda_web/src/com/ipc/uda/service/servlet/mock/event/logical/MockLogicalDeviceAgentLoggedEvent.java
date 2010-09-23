/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.event.logical;

import java.util.List;

import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.logicalDevice.AgentID;
import com.ipc.va.cti.logicalDevice.CDIClientType;
import com.ipc.va.cti.logicalDevice.RecordProfile;
import com.ipc.va.cti.logicalDevice.RecordProtocolType;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceAgentLoggedEvent;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;

/**
 * @author mordarsd
 *
 */
public class MockLogicalDeviceAgentLoggedEvent extends MockLogicalDeviceEvent implements LogicalDeviceAgentLoggedEvent
{

    private CDIClientType clientType;
    private List<RecordProfile> recordProfile;
    private RecordProtocolType recordProtocol;
    
    
    public MockLogicalDeviceAgentLoggedEvent()
    {
        this(null, null, null);
    }
    
    /**
     * @param clientType
     * @param recordProfile
     * @param recordProtocol
     */
    public MockLogicalDeviceAgentLoggedEvent(CDIClientType clientType, List<RecordProfile> recordProfile,
            RecordProtocolType recordProtocol)
    {
        this(null, null, clientType, recordProfile, recordProtocol);
    }

    /**
     * @param agentID
     * @param deviceID
     * @param clientType
     * @param recordProfile
     * @param recordProtocol
     */
    public MockLogicalDeviceAgentLoggedEvent(AgentID agentID, DeviceID deviceID, CDIClientType clientType,
            List<RecordProfile> recordProfile, RecordProtocolType recordProtocol)
    {
        this(null, null, agentID, deviceID, clientType, recordProfile, recordProtocol);
    }

    /**
     * @param monitorCrossRefID
     * @param notifyToURL
     * @param agentID
     * @param deviceID
     * @param clientType
     * @param recordProfile
     * @param recordProtocol
     */
    public MockLogicalDeviceAgentLoggedEvent(MonitorCrossRefID monitorCrossRefID, String notifyToURL, AgentID agentID,
            DeviceID deviceID, CDIClientType clientType, List<RecordProfile> recordProfile, RecordProtocolType recordProtocol)
    {
        super(monitorCrossRefID,notifyToURL,agentID,deviceID);
        this.clientType = clientType;
        this.recordProfile = recordProfile;
        this.recordProtocol = recordProtocol;
    }

    @Override
    public CDIClientType getClientType()
    {
        return this.clientType;
    }

    @Override
    public List<RecordProfile> getRecordProfile()
    {
        return this.recordProfile;
    }

    @Override
    public RecordProtocolType getRecordProtocol()
    {
        return this.recordProtocol;
    }

    /**
     * @param clientType the clientType to set
     */
    public void setClientType(CDIClientType clientType)
    {
        this.clientType = clientType;
    }

    /**
     * @param recordProfile the recordProfile to set
     */
    public void setRecordProfile(List<RecordProfile> recordProfile)
    {
        this.recordProfile = recordProfile;
    }

    /**
     * @param recordProtocol the recordProtocol to set
     */
    public void setRecordProtocol(RecordProtocolType recordProtocol)
    {
        this.recordProtocol = recordProtocol;
    }

    
    

}
