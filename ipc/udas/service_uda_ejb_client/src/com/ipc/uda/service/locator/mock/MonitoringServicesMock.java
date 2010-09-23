/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.locator.mock;

import com.ipc.va.cti.CtiException;
import com.ipc.va.cti.ListenerConfig;
import com.ipc.va.cti.ResultStatusType;
import com.ipc.va.cti.callControl.events.CallControlListener;
import com.ipc.va.cti.lineStatus.events.LineStatusListener;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;
import com.ipc.va.cti.monitoring.MonitorFilter;
import com.ipc.va.cti.monitoring.MonitorObject;
import com.ipc.va.cti.monitoring.MonitorType;
import com.ipc.va.cti.monitoring.events.MonitorListener;
import com.ipc.va.cti.monitoring.services.MonitoringServices;
import com.ipc.va.cti.monitoring.services.extensions.MonitorRefreshExtensions;
import com.ipc.va.cti.monitoring.services.extensions.MonitorStartExtensions;
import com.ipc.va.cti.monitoring.services.extensions.MonitorStopExtensions;
import com.ipc.va.cti.monitoring.services.results.MonitorRefreshResult;
import com.ipc.va.cti.monitoring.services.results.MonitorStartResult;
import com.ipc.va.cti.monitoring.services.results.MonitorStopResult;
import com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener;
import com.ipc.va.cti.presence.events.PresenceListener;

/**
 * @author mordarsd
 *
 */
public class MonitoringServicesMock implements MonitoringServices
{

    @Override
    public void addCallControlListener(MonitorCrossRefID crossRefIdentifier, ListenerConfig listenerConfig,
            CallControlListener listener) throws Exception, CtiException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addLineStatusListener(MonitorCrossRefID crossRefIdentifier, ListenerConfig listenerConfig,
            LineStatusListener listener) throws Exception, CtiException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addLogicalDeviceListener(MonitorCrossRefID crossRefIdentifier, ListenerConfig listenerConfig,
            LogicalDeviceListener listener) throws Exception, CtiException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addMonitorListener(MonitorCrossRefID crossRefIdentifier, ListenerConfig listenerConfig, MonitorListener listener)
            throws Exception, CtiException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addPhysicalDeviceListener(MonitorCrossRefID crossRefIdentifier, ListenerConfig listenerConfig,
            PhysicalDeviceListener listener) throws Exception, CtiException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public MonitorRefreshResult monitorRefresh(MonitorCrossRefID crossRefIdentifier, MonitorRefreshExtensions extensions)
            throws Exception, CtiException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MonitorStartResult monitorStart(MonitorObject monitorObject, MonitorFilter requestedMonitorFilter,
            MonitorType monitorType, MonitorStartExtensions extensions) throws Exception, CtiException
    {
        
        return new MonitorStartResult()
        {
            @Override
            public MonitorCrossRefID getCrossRefIdentifier()
            {
                return new MonitorCrossRefID()
                {
                    @Override
                    public String getMonitorCrossRefId()
                    {
                        return "777";
                    }  
                };
            }
            
            @Override
            public ResultStatusType getResultStatus()
            {
                return ResultStatusType.SUCCESS;
            }
        };
    }

    @Override
    public MonitorStopResult monitorStop(MonitorCrossRefID crossRefIdentifier, MonitorStopExtensions extensions)
            throws Exception, CtiException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addPresenceStatusListener(MonitorCrossRefID crossRefIdentifier,
            ListenerConfig listenerConfig, PresenceListener listener) throws Exception,
            CtiException
    {
        // TODO Auto-generated method stub
        
    }

 

}
