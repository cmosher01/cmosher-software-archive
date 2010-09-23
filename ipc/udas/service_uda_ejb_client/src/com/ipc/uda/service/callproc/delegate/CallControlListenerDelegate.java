/**
 * 
 */
package com.ipc.uda.service.callproc.delegate;

import com.ipc.uda.service.locator.ServiceLocatorProvider;
import com.ipc.uda.service.locator.spi.UdaServiceLocator;
import com.ipc.va.cti.callControl.events.CallControlConferencedEvent;
import com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent;
import com.ipc.va.cti.callControl.events.CallControlDeliveredEvent;
import com.ipc.va.cti.callControl.events.CallControlEstablishedEvent;
import com.ipc.va.cti.callControl.events.CallControlEvent;
import com.ipc.va.cti.callControl.events.CallControlFailedEvent;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;
import com.ipc.va.cti.callControl.events.CallControlListener;
import com.ipc.va.cti.callControl.events.CallControlMutingEvent;
import com.ipc.va.cti.callControl.events.CallControlOfferedEvent;
import com.ipc.va.cti.callControl.events.CallControlOriginatedEvent;
import com.ipc.va.cti.callControl.events.CallControlVoiceActivityEvent;

/**
 * @author mordarsd
 *
 */
public class CallControlListenerDelegate implements CallControlListener
{
    
    private static final CallControlListener delegate = 
        ServiceLocatorProvider.getUdaServiceLocator().getCallControlListener();

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#bridgedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void bridgedEvent(CallControlEvent event)
    {
        delegate.bridgedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#callClearedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void callClearedEvent(CallControlEvent event)
    {
        delegate.callClearedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#callInformationEvent(com.ipc.va.cti.callControl.events.CallControlInformationEvent)
     */
    public void callInformationEvent(CallControlInformationEvent event)
    {
        delegate.callInformationEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#chargingEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void chargingEvent(CallControlEvent event)
    {
        delegate.chargingEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#connectionClearedEvent(com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent)
     */
    public void connectionClearedEvent(CallControlConnectionClearedEvent event)
    {
        delegate.connectionClearedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#deliveredEvent(com.ipc.va.cti.callControl.events.CallControlDeliveredEvent)
     */
    public void deliveredEvent(CallControlDeliveredEvent event)
    {
        delegate.deliveredEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#digitsDialedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void digitsDialedEvent(CallControlEvent event)
    {
        delegate.digitsDialedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#digitsGeneratedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void digitsGeneratedEvent(CallControlEvent event)
    {
        delegate.digitsGeneratedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#divertedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void divertedEvent(CallControlEvent event)
    {
        delegate.divertedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#establishedEvent(com.ipc.va.cti.callControl.events.CallControlEstablishedEvent)
     */
    public void establishedEvent(CallControlEstablishedEvent event)
    {
        delegate.establishedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#failedEvent(com.ipc.va.cti.callControl.events.CallControlFailedEvent)
     */
    public void failedEvent(CallControlFailedEvent event)
    {
        delegate.failedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#heldEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void heldEvent(CallControlEvent event)
    {
        delegate.heldEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#networkCapabilitiesChangedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void networkCapabilitiesChangedEvent(CallControlEvent event)
    {
        delegate.networkCapabilitiesChangedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#networkReachedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void networkReachedEvent(CallControlEvent event)
    {
        delegate.networkReachedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#offeredEvent(com.ipc.va.cti.callControl.events.CallControlOfferedEvent)
     */
    public void offeredEvent(CallControlOfferedEvent event)
    {
        delegate.offeredEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#originatedEvent(com.ipc.va.cti.callControl.events.CallControlOriginatedEvent)
     */
    public void originatedEvent(CallControlOriginatedEvent event)
    {
        delegate.originatedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#queuedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void queuedEvent(CallControlEvent event)
    {
        delegate.queuedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#retrievedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void retrievedEvent(CallControlEvent event)
    {
        delegate.retrievedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#serviceCompletionFailureEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void serviceCompletionFailureEvent(CallControlEvent event)
    {
        delegate.serviceCompletionFailureEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#serviceInitiatedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void serviceInitiatedEvent(CallControlEvent event)
    {
        delegate.serviceInitiatedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#telephonyTonesGeneratedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void telephonyTonesGeneratedEvent(CallControlEvent event)
    {
        delegate.telephonyTonesGeneratedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.callControl.events.CallControlListener#transferedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    public void transferedEvent(CallControlEvent event)
    {
        delegate.transferedEvent(event);
    }

	@Override
	public void conferencedEvent(CallControlConferencedEvent event) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void mutingEvent(CallControlMutingEvent event)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void voiceActivityEvent(CallControlVoiceActivityEvent event)
    {
        // TODO Auto-generated method stub
        
    }

    



}
