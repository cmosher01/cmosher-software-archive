/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc.delegate;

import com.ipc.uda.service.locator.ServiceLocatorProvider;
import com.ipc.va.cti.lineStatus.events.LineStatusEvent;
import com.ipc.va.cti.lineStatus.events.LineStatusListener;

/**
 * @author mordarsd
 *
 */
public class LineStatusListenerDelegate implements LineStatusListener
{

    private static final LineStatusListener delegate = 
        ServiceLocatorProvider.getUdaServiceLocator().getLineStatusListener();
    
    /* (non-Javadoc)
     * @see com.ipc.va.cti.lineStatus.events.LineStatusListener#lineStatusUpdate(com.ipc.va.cti.lineStatus.events.LineStatusEvent)
     */
    @Override
    public void lineStatusUpdate(LineStatusEvent event)
    {
        delegate.lineStatusUpdate(event);
    }

}
