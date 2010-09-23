/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.locator.mock;

import com.ipc.va.cti.CtiException;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.ResultStatusType;
import com.ipc.va.cti.logicalDevice.AgentID;
import com.ipc.va.cti.logicalDevice.AgentState;
import com.ipc.va.cti.logicalDevice.ForwardType;
import com.ipc.va.cti.logicalDevice.services.LogicalDeviceServices;
import com.ipc.va.cti.logicalDevice.services.extensions.GetAgentStateExtensions;
import com.ipc.va.cti.logicalDevice.services.extensions.SetAgentStateExtensions;
import com.ipc.va.cti.logicalDevice.services.extensions.SetDoNotDisturbExtensions;
import com.ipc.va.cti.logicalDevice.services.extensions.SetForwardingExtensions;
import com.ipc.va.cti.logicalDevice.services.results.GetAgentStateResult;
import com.ipc.va.cti.logicalDevice.services.results.SetAgentStateResult;
import com.ipc.va.cti.logicalDevice.services.results.SetAgentStateResultImpl;
import com.ipc.va.cti.logicalDevice.services.results.SetDoNotDisturbResult;
import com.ipc.va.cti.logicalDevice.services.results.SetForwardingResult;

/**
 * @author mordarsd
 *
 */
public class LogicalDeviceServicesMock implements LogicalDeviceServices
{

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.services.LogicalDeviceServices#getAgentState(com.ipc.va.cti.DeviceID, com.ipc.va.cti.logicalDevice.services.extensions.GetAgentStateExtensions)
     */
    @Override
    public GetAgentStateResult getAgentState(DeviceID deviceId, GetAgentStateExtensions extensions) throws CtiException,
            Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.services.LogicalDeviceServices#setAgentState(com.ipc.va.cti.DeviceID, com.ipc.va.cti.logicalDevice.AgentState, com.ipc.va.cti.logicalDevice.AgentID, com.ipc.va.cti.logicalDevice.services.extensions.SetAgentStateExtensions)
     */
    @Override
    public SetAgentStateResult setAgentState(DeviceID deviceId, AgentState requestedAgentState, AgentID agentId,
            SetAgentStateExtensions extensions) throws CtiException, Exception
    {
        return new SetAgentStateResult() {
            @Override
            public ResultStatusType getResultStatus()
            {
                return ResultStatusType.SUCCESS;
            }
        };
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.services.LogicalDeviceServices#setDoNotDisturb(com.ipc.va.cti.DeviceID, boolean, com.ipc.va.cti.logicalDevice.services.extensions.SetDoNotDisturbExtensions)
     */
    @Override
    public SetDoNotDisturbResult setDoNotDisturb(DeviceID deviceId, boolean doNotDisturbOn, SetDoNotDisturbExtensions extensions)
            throws CtiException, Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.services.LogicalDeviceServices#setForwarding(com.ipc.va.cti.DeviceID, com.ipc.va.cti.logicalDevice.ForwardType, boolean, com.ipc.va.cti.DeviceID, int, int, com.ipc.va.cti.logicalDevice.services.extensions.SetForwardingExtensions)
     */
    @Override
    public SetForwardingResult setForwarding(DeviceID deviceId, ForwardType forwardType, boolean activateForward,
            DeviceID forwardDN, int ringCount, int ringDuration, SetForwardingExtensions extensions) throws CtiException,
            Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GetAgentStateResult getAgentState(AgentID agentId, GetAgentStateExtensions extensions)
            throws CtiException, Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

}
