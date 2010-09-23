/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;

/**
 * @author mordarsd
 *
 */
public interface ButtonPressCall extends Call
{

    void udacButtonPressed();
    
    PrivateWireFsmContext.PrivateWireState  getState();
    
    
}
