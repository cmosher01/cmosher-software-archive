/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.locator;

import com.ipc.uda.service.locator.mbean.ServiceLocatorConfig;


/**
 * Task, that runs at application startup time, that sets the current location
 * of the CTI services.
 * 
 * @author mordarsd
 * @author mosherc
 */
public class ServiceLocatorStartupTask extends AbstractServiceLocatorLifecycleTask
{

    private static final String CTI_PROVIDER_HOSTNAME_PARAM = "uda.defaults.cti.service.provider.hostname";
    private static final String CTI_PROVIDER_PORT_PARAM = "uda.defaults.cti.service.provider.port";

    public ServiceLocatorStartupTask()
    {
        super("CtiServiceLocatorConfigMBean");
    }
    
    @Override
    protected void configureServiceLocator(ServiceLocatorConfig cfg)
    {
        ServiceLocatorProvider.configureCtiServiceLocator(cfg);
    }
    
    @Override
    protected String getProviderHostnameParam()
    {
        return CTI_PROVIDER_HOSTNAME_PARAM;
    }
    
    @Override
    protected String getProviderPortParam()
    {
        return CTI_PROVIDER_PORT_PARAM;
    }
    
}
