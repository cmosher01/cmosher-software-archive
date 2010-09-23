/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.callproc;

import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.Event;
import com.ipc.uda.types.ForcedLogOffRequestEvent;
import com.ipc.uda.types.ForcedLogOffResultEvent;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceAgentLoggedEvent;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener;

/**
 * @author mordarsd
 *
 */
public class LogicalDeviceListenerImpl implements LogicalDeviceListener
{
    
    
    private static final LogicalDeviceListenerImpl instance = new LogicalDeviceListenerImpl();
    
    private LogicalDeviceListenerImpl()
    {
        // this is a singleton
    }

    /**
     * Sole instance getter
     *     
     * @return
     */
    public static LogicalDeviceListenerImpl getInstance()
    {
        return instance;
    }
    
    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentBusy(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void agentBusy(LogicalDeviceEvent event)
    {
        //final UserContext userContext = this.getUserContextFromEvent(event);
        
        
        
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentLoggedOff(com.ipc.va.cti.logicalDevice.events.LogicalDeviceAgentLoggedEvent)
     */
    @Override
    public void agentLoggedOff(LogicalDeviceAgentLoggedEvent event)
    {
        final Optional<UserContext> optCtx = UserContextManager.getInstance().getUserContext(event.getMonitorCrossRefID());
        if (!optCtx.exists())
        {
            Log.logger().info("Received agentLoggedOff event for unknown user: " + event.getAgentID().getAgentID());
            return;
        }
        
        final Event udacEvent = new Event();
        
        final ForcedLogOffResultEvent florEvent = new ForcedLogOffResultEvent();
        udacEvent.setForcedLogOffResult(florEvent);

        optCtx.get().getEventQueue().send(udacEvent);
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentLoggedOn(com.ipc.va.cti.logicalDevice.events.LogicalDeviceAgentLoggedEvent)
     */
    @Override
    public void agentLoggedOn(LogicalDeviceAgentLoggedEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentNotReady(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void agentNotReady(LogicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentReady(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void agentReady(LogicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentWorkingAfterCall(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void agentWorkingAfterCall(LogicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#autoAnswerStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void autoAnswerStatus(LogicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#autoWorkModeStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void autoWorkModeStatus(LogicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#callBackMessageStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void callBackMessageStatus(LogicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#callBackStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void callBackStatus(LogicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#callerIDStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void callerIDStatus(LogicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#doNotDisturbStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void doNotDisturbStatus(LogicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#forwardingStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void forwardingStatus(LogicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#routingModeStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void routingModeStatus(LogicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

	@Override
	public void agentForcedLoggedOff(LogicalDeviceEvent event) 
	{
	    
        final Optional<UserContext> optCtx = UserContextManager.getInstance().getUserContext(event.getMonitorCrossRefID());
        if (!optCtx.exists())
        {
            Log.logger().info("Received agentForcedLoggedOff event for unknown user: " + event.getAgentID().getAgentID());
            return;
        }
        
		final Event udacEvent = new Event();
		
		ForcedLogOffRequestEvent value = new ForcedLogOffRequestEvent();
			
		udacEvent.setForcedLogOffRequest(value);
		
		optCtx.get().getEventQueue().send(udacEvent);
		
	}
	
	
}
