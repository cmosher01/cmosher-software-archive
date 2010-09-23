/**
 * 
 */
package com.ipc.uda.service.callproc.delegate;

import com.ipc.uda.service.locator.ServiceLocatorProvider;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceAgentLoggedEvent;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener;

/**
 * @author mordarsd
 *
 */
public class LogicalDeviceListenerDelegate implements LogicalDeviceListener
{

    private static final LogicalDeviceListener delegate = 
        ServiceLocatorProvider.getUdaServiceLocator().getLogicalDeviceListener();

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentBusy(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void agentBusy(LogicalDeviceEvent event)
    {
        delegate.agentBusy(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentLoggedOff(com.ipc.va.cti.logicalDevice.events.LogicalDeviceAgentLoggedEvent)
     */
    @Override
    public void agentLoggedOff(LogicalDeviceAgentLoggedEvent event)
    {
        delegate.agentLoggedOff(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentLoggedOn(com.ipc.va.cti.logicalDevice.events.LogicalDeviceAgentLoggedEvent)
     */
    @Override
    public void agentLoggedOn(LogicalDeviceAgentLoggedEvent event)
    {
        delegate.agentLoggedOn(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentNotReady(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void agentNotReady(LogicalDeviceEvent event)
    {
        delegate.agentNotReady(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentReady(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void agentReady(LogicalDeviceEvent event)
    {
        delegate.agentReady(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#agentWorkingAfterCall(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void agentWorkingAfterCall(LogicalDeviceEvent event)
    {
        delegate.agentWorkingAfterCall(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#autoAnswerStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void autoAnswerStatus(LogicalDeviceEvent event)
    {
        delegate.autoAnswerStatus(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#autoWorkModeStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void autoWorkModeStatus(LogicalDeviceEvent event)
    {
        delegate.autoWorkModeStatus(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#callBackMessageStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void callBackMessageStatus(LogicalDeviceEvent event)
    {
        delegate.callBackMessageStatus(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#callBackStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void callBackStatus(LogicalDeviceEvent event)
    {
        delegate.callBackStatus(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#callerIDStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void callerIDStatus(LogicalDeviceEvent event)
    {
        delegate.callerIDStatus(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#doNotDisturbStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void doNotDisturbStatus(LogicalDeviceEvent event)
    {
        delegate.doNotDisturbStatus(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#forwardingStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void forwardingStatus(LogicalDeviceEvent event)
    {
        delegate.forwardingStatus(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener#routingModeStatus(com.ipc.va.cti.logicalDevice.events.LogicalDeviceEvent)
     */
    @Override
    public void routingModeStatus(LogicalDeviceEvent event)
    {
        delegate.routingModeStatus(event);
    }

	@Override
	public void agentForcedLoggedOff(LogicalDeviceEvent event)
	{
	    delegate.agentForcedLoggedOff(event);
	}
}
