/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types.util;

import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.locator.CtiServiceLocator;
import com.ipc.uda.service.locator.ServiceLocatorProperties;
import com.ipc.uda.service.locator.ServiceLocatorProvider;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.jmx.JMXUtil;
import com.ipc.uda.types.LogOffQueryResult;
import com.ipc.uda.types.LogOnQueryResult;
import com.ipc.uda.types.QueryResult;
import com.ipc.uda.types.StatusType;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.DeviceIDImpl;
import com.ipc.va.cti.ResultStatusType;
import com.ipc.va.cti.logicalDevice.AgentID;
import com.ipc.va.cti.logicalDevice.AgentIDImpl;
import com.ipc.va.cti.logicalDevice.AgentState;
import com.ipc.va.cti.logicalDevice.CDILoginType;
import com.ipc.va.cti.logicalDevice.services.LogicalDeviceServices;
import com.ipc.va.cti.logicalDevice.services.extensions.SetAgentStateExtensions;
import com.ipc.va.cti.logicalDevice.services.extensions.SetAgentStateExtensionsImpl;
import com.ipc.va.cti.logicalDevice.services.results.SetAgentStateResult;

/**
 * Utility class for performing LogOn/Off operations.
 * 
 * @author mordarsd
 *
 */
public final class LifecycleUtil
{

    // REVIEW 0 update javadocs
    
    
    /**
     * Logs off a User and removes their associated UserContext.
     * 
     * @param userCtx
     * @param strDeviceID
     * @return
     * @throws UserStateChangeException
     */
    public static Optional<ExecutionResult> logOff(final UserContext userCtx)
        throws UserStateChangeException
    {
        return logOff(userCtx, true);
    }
    
    /**
     * Logs off a User with the option to remove their associated UserContext
     * 
     * @param userCtx
     * @param strDeviceID
     * @param removeUserContext
     * @return
     */
    public static Optional<ExecutionResult> logOff(final UserContext userCtx, boolean removeUserContext)
        throws UserStateChangeException
    {
        
        final QueryResult queryResult = new QueryResult();
        try 
        {
            LogicalDeviceServices logicalDeviceServices = getLogicalDeviceServices(userCtx);
            // REVIEW 0 s/b declared and initialized below
            SetAgentStateResult agentStateResult;
    
            AgentID agentID = new AgentIDImpl(userCtx.getUser().getName());
            DeviceID deviceID = new DeviceIDImpl(userCtx.getCurrentDeviceID());
            SetAgentStateExtensionsImpl sase = new SetAgentStateExtensionsImpl(JMXUtil.getInstanceListenIP(),
                    CDILoginType.STANDARD);
    
            agentStateResult = logicalDeviceServices.setAgentState(deviceID,AgentState.LOGGED_OFF,agentID,sase);
    
            ResultStatusType status = agentStateResult.getResultStatus();
    
            final LogOffQueryResult result = new LogOffQueryResult();
            
            switch(status)
            {
                case SUCCESS:
                    result.setStatus(StatusType.SUCCESS);
                    break;
                    
                case FAILURE:
                default:
                    result.setStatus(StatusType.FAILURE);
                    break;
                    
            }
            
            result.setMessage(status.value()); 
            
            queryResult.setLogOffResult(result);

        }
        catch (final Throwable e)
        {
            throw new UserStateChangeException(
                    userCtx.getUser().getName(), userCtx.getCurrentDeviceID(), 
                    AgentState.LOGGED_OFF.toString(), 
                    e.getMessage(), e);
        } 
        finally
        {
            if (removeUserContext)
            {
                UserContextManager.getInstance().removeContext(userCtx.getUserID());
            }
        }
        
        return new Optional<ExecutionResult>(queryResult);
    }
    
    
    /**
     * Logs on a User with a given UserContext and deviceID
     * 
     * @param userCtx
     * @param strDeviceID
     * @return
     * @throws UserStateChangeException
     */
    public static Optional<ExecutionResult> logOn(final UserContext userCtx)
        throws UserStateChangeException
    {
        final QueryResult queryResult = new QueryResult();
        try {
            final LogicalDeviceServices logicalDeviceServices = getLogicalDeviceServices(userCtx);
    
            final AgentID agentID = new AgentIDImpl(userCtx.getUser().getName());
            final DeviceID devID = new DeviceIDImpl(userCtx.getCurrentDeviceID());
    
            String listenAddr = JMXUtil.getInstanceListenIP();
            
            // REVIEW 0 change to intention revealing variable name - not "self addressed stamped envelope" :) 
            final SetAgentStateExtensions sase =
                new SetAgentStateExtensionsImpl(listenAddr, CDILoginType.STANDARD);
    
            final SetAgentStateResult agentStateResult = logicalDeviceServices.setAgentState(devID, AgentState.LOGGED_ON, agentID, sase);

            final LogOnQueryResult result = new LogOnQueryResult();
            ResultStatusType status = agentStateResult.getResultStatus();
            
            switch(status)
            {
                case SUCCESS:
                    result.setStatus(StatusType.SUCCESS);
                    break;
                    
                case FAILURE:
                    result.setStatus(StatusType.FAILURE);
                    break;
                    
                case USER_ALREADY_LOGGED_ON:
                    result.setStatus(StatusType.ALREADY_LOGGED_ON);
                    break;
            }
            
            result.setMessage(status.value()); 
            
            queryResult.setLogOnResult(result);
        } 
        catch (final Throwable e)
        {
            throw new UserStateChangeException(
                    userCtx.getUser().getName(), userCtx.getCurrentDeviceID(), 
                    AgentState.LOGGED_ON.toString(), 
                    e.getMessage(), e);
        }
        
        return new Optional<ExecutionResult>(queryResult);
    }


    /**
     * Executes a <i>force</i> log-off.
     * @param userCtx the user to log off
     * @return {@link Nothing}
     * @throws UserStateChangeException if the user cannot be force logged-off
     */
    public static Optional<ExecutionResult> forceLogOff(final UserContext userCtx)
        throws UserStateChangeException
    {     
        
        // REVIEW 0 fix indentation  
    	 final AgentID agentID = new AgentIDImpl(userCtx.getUser().getName());
    	 SetAgentStateExtensionsImpl sase = new SetAgentStateExtensionsImpl(JMXUtil.getInstanceListenIP(),
                 CDILoginType.STANDARD);

    	 LogicalDeviceServices logicalDeviceServices = getLogicalDeviceServices(userCtx);
         SetAgentStateResult agentStateResult;
       try 
         {
			agentStateResult = logicalDeviceServices.setAgentState(null,AgentState.FORCED_LOGGED_OFF,agentID,sase);
		} 
         catch (final Throwable e)
         {
             throw new UserStateChangeException(
            		 userCtx.getUser().getName(), null, 
                     AgentState.FORCED_LOGGED_OFF.toString(), 
                     e.getMessage(), e);
         }
 
         ResultStatusType status = agentStateResult.getResultStatus();

         // REVIEW 0 change to use 'if' statement
         switch(status)
         {
             case FAILURE:
                 throw new UserStateChangeException(
                         userCtx.getUser().getName(), null, 
                         AgentState.FORCED_LOGGED_OFF.toString(), 
                         " FORCED_LOGGED_OFF Operation resulted Failure from CTI" , new Exception("setAgentState(FORCED_LOGGED_OFF) resulted FAILURE"));

                 
         }
         
         // Since this is invoked from the a Command, there is no ExecutionResult
         return new Nothing<ExecutionResult>();
    }
    
    private static LogicalDeviceServices getLogicalDeviceServices(final UserContext userCtx)
    {
        final CtiServiceLocator ctiLocator = ServiceLocatorProvider.getCtiServiceLocator();
        final ServiceLocatorProperties props = new ServiceLocatorProperties();
        props.setUsingMockObjects(userCtx.isUsingMockObjects());
        return ctiLocator.getLogicalDeviceServices(props);
    }

    
}
