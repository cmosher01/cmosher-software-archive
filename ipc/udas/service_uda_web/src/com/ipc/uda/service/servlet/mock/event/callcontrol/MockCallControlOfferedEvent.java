/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.event.callcontrol;

import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.UserData;
import com.ipc.va.cti.callControl.events.CallControlOfferedEvent;
import com.ipc.va.cti.callControl.services.extensions.MakeCallExtensions;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;

/**
 * @author mordarsd
 *
 */
public class MockCallControlOfferedEvent implements CallControlOfferedEvent
{

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlOfferedEvent#getCalledDeviceID()
     */
    @Override
    public DeviceID getCalledDeviceID()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlOfferedEvent#getCallingDeviceID()
     */
    @Override
    public DeviceID getCallingDeviceID()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlOfferedEvent#getLastRedirectionDeviceID()
     */
    @Override
    public DeviceID getLastRedirectionDeviceID()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlOfferedEvent#getOfferedDeviceID()
     */
    @Override
    public DeviceID getOfferedDeviceID()
    {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public MakeCallExtensions getCallExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionID getConnectionID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventCause getEventCause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserData getUserData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MonitorCrossRefID getMonitorCrossRefID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNotifyToURL() {
		// TODO Auto-generated method stub
		return null;
	}

}
