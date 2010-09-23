/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock;

import com.ipc.va.cti.CtiEvent;

/**
 * @author mordarsd
 *
 */
public interface MockCtiEvent extends CtiEvent
{

    public void setProperty(String name, String value);
    
    
}
