/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.locator;

import com.ipc.uda.service.locator.mbean.ServiceLocatorConfig;
import com.ipc.uda.service.locator.spi.UdaServiceLocator;



/**
 * @author mordarsd
 * @author mosherc
 */
public final class ServiceLocatorProvider
{
    private static final CtiServiceLocator ctiServiceLocator = new CtiServiceLocator();
    private static final UdaServiceLocator udaServiceLocator = new UdaServiceLocator();

    /**
     * 
     * @param location
     */
    static void configureCtiServiceLocator(final ServiceLocatorConfig config)
    {
        ServiceLocatorProvider.ctiServiceLocator.configure(config);
    }

    static void configureUdaServiceLocator(final ServiceLocatorConfig config)
    {
        ServiceLocatorProvider.udaServiceLocator.configure(config);
    }


    /**
     * Returns the ServiceLocator for CTI Service Location
     * 
     * @return
     */
    public static CtiServiceLocator getCtiServiceLocator()
    {
        return ServiceLocatorProvider.ctiServiceLocator;
    }
    
    /**
     * Returns the ServiceLocator for the UDA Service Location
     * 
     * @return
     */
    public static UdaServiceLocator getUdaServiceLocator()
    {
        return ServiceLocatorProvider.udaServiceLocator;
    }
}
