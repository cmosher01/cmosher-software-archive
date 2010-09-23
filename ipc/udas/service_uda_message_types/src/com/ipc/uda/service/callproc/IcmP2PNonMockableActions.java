/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;

import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.callControl.events.CallControlOfferedEvent;

/**
 * @author mosherc
 *
 */
public class IcmP2PNonMockableActions
{
    protected String aor;
    protected ConnectionID connectionID;

    void setAor(final String aor)
    {
        this.aor = aor;
    }

    void setOfferedAor(final CallControlOfferedEvent offered)
    {
        this.aor = offered.getCallingDeviceID().getDeviceID();
        this.connectionID = offered.getConnectionID();
    }

    void autoUdaAccept(final IcmP2PFsmContext context)
    {
        context.udaAccept();
    }

    void autoUdaNull(final IcmP2PFsmContext context)
    {
        context.udaNull();
    }

    @Override
    public String toString()
    {
        // an identifying name, for use by the log file
        if (this.connectionID != null)
        {
            final String connID = this.connectionID.getConnectionID();
            if (connID != null && !connID.isEmpty())
            {
                return connID;
            }
        }
        if (this.aor != null && !this.aor.isEmpty())
        {
            return this.aor;
        }
        return super.toString();
    }
}
