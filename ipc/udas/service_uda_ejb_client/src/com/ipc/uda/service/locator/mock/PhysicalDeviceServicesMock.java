/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.locator.mock;

import com.ipc.va.cti.CtiException;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.callControl.AudioDeviceChannel;
import com.ipc.va.cti.callControl.AudioDeviceType;
import com.ipc.va.cti.physicalDevice.services.PhysicalDeviceServices;
import com.ipc.va.cti.physicalDevice.services.results.GetMessageWaitingResult;
import com.ipc.va.cti.physicalDevice.services.results.SetVolumeResult;

/**
 * @author mordarsd
 *
 */
public class PhysicalDeviceServicesMock implements PhysicalDeviceServices
{

    /* (non-Javadoc)
     * @see com.ipc.va.cti.physicalDevice.services.PhysicalDeviceServices#getMessageWaitingIndicator(com.ipc.va.cti.DeviceID)
     */
    @Override
    public GetMessageWaitingResult getMessageWaitingIndicator(DeviceID deviceId) throws CtiException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SetVolumeResult setVolume(String userAOR, AudioDeviceType device,
            AudioDeviceChannel channel, int volume) throws CtiException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
