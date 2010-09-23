/**
 * 
 */
package com.ipc.uda.service.callproc;

import com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent;
import com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener;
import com.ipc.va.cti.physicalDevice.events.PhysicalDeviceMessageWaitingEvent;

/**
 * @author mordarsd
 *
 */
public class PhysicalDeviceListenerImpl implements PhysicalDeviceListener
{
    
    
    private static final PhysicalDeviceListenerImpl instance = new PhysicalDeviceListenerImpl();
    
    private PhysicalDeviceListenerImpl()
    {
    }

    /**
     * Sole instance getter
     *     
     * @return
     */
    public static PhysicalDeviceListenerImpl getInstance()
    {
        return instance;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#buttonInformationEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    @Override
    public void buttonInformationEvent(PhysicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#buttonPressEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    @Override
    public void buttonPressEvent(PhysicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#displayUpdatedEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    @Override
    public void displayUpdatedEvent(PhysicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#hookSwitchEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    @Override
    public void hookSwitchEvent(PhysicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#lampModeEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    @Override
    public void lampModeEvent(PhysicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#messageWaitingEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceMessageWaitingEvent)
     */
    @Override
    public void messageWaitingEvent(PhysicalDeviceMessageWaitingEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#microphoneGainEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    @Override
    public void microphoneGainEvent(PhysicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#microphoneMuteEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    @Override
    public void microphoneMuteEvent(PhysicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#ringerStatusEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    @Override
    public void ringerStatusEvent(PhysicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#speakerMuteEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    @Override
    public void speakerMuteEvent(PhysicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener#speakerVolumeEvent(com.ipc.va.cti.physicalDevice.events.PhysicalDeviceEvent)
     */
    @Override
    public void speakerVolumeEvent(PhysicalDeviceEvent event)
    {
        // TODO Auto-generated method stub

    }

}
