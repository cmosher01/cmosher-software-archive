/**
 * 
 */
package com.ipc.uda.service.callproc.delegate;

import com.ipc.uda.service.locator.ServiceLocatorProvider;
import com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent;
import com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener;
import com.ipc.va.cti.physicalDevice.events.PhysicalDeviceMessageWaitingEvent;

/**
 * @author mordarsd
 *
 */
public class PhysicalDeviceListenerDelegate implements PhysicalDeviceListener
{
    
    private static final PhysicalDeviceListener delegate = 
        ServiceLocatorProvider.getUdaServiceLocator().getPhysicalDeviceListener();

    /**
     * @param event
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#buttonInformationEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    public void buttonInformationEvent(PhysicalDeviceEvent event)
    {
        delegate.buttonInformationEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#buttonPressEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    public void buttonPressEvent(PhysicalDeviceEvent event)
    {
        delegate.buttonPressEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#displayUpdatedEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    public void displayUpdatedEvent(PhysicalDeviceEvent event)
    {
        delegate.displayUpdatedEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#hookSwitchEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    public void hookSwitchEvent(PhysicalDeviceEvent event)
    {
        delegate.hookSwitchEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#lampModeEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    public void lampModeEvent(PhysicalDeviceEvent event)
    {
        delegate.lampModeEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#messageWaitingEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceMessageWaitingEvent)
     */
    public void messageWaitingEvent(PhysicalDeviceMessageWaitingEvent event)
    {
        delegate.messageWaitingEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#microphoneGainEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    public void microphoneGainEvent(PhysicalDeviceEvent event)
    {
        delegate.microphoneGainEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#microphoneMuteEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    public void microphoneMuteEvent(PhysicalDeviceEvent event)
    {
        delegate.microphoneMuteEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#ringerStatusEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    public void ringerStatusEvent(PhysicalDeviceEvent event)
    {
        delegate.ringerStatusEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#speakerMuteEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    public void speakerMuteEvent(PhysicalDeviceEvent event)
    {
        delegate.speakerMuteEvent(event);
    }

    /**
     * @param event
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#speakerVolumeEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    public void speakerVolumeEvent(PhysicalDeviceEvent event)
    {
        delegate.speakerVolumeEvent(event);
    }

   
    
    
}
