/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.locator;

import com.ipc.uda.service.locator.mbean.ServiceLocatorConfig;



/**
 * @author mordarsd
 * 
 */
public interface ServiceLocator
{
    /**
     * 
     * @param config
     */
    void configure(ServiceLocatorConfig config);
}
