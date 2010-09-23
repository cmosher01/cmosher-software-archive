/**
 * 
 */
package com.ipc.uda.service.locator;

import weblogic.application.ApplicationContext;

import com.ipc.uda.service.lifecycle.LifecycleException;
import com.ipc.uda.service.lifecycle.LifecycleTask;
import com.ipc.uda.service.locator.mbean.ServiceLocatorConfig;

/**
 * @author mordarsd
 *
 */
public class UdaServiceLocatorStartupTask extends AbstractServiceLocatorLifecycleTask
{

    private static final String UDA_PROVIDER_HOSTNAME_PARAM = "uda.defaults.uda.service.provider.hostname";
    private static final String UDA_PROVIDER_PORT_PARAM = "uda.defaults.uda.service.provider.port";

    

    public UdaServiceLocatorStartupTask()
    {
        super("UdaServiceLocatorConfigMBean");
    }
    
    /* (non-Javadoc)
     * @see com.ipc.uda.service.locator.AbstractServiceLocatorLifecycleTask#configureServiceLocator(com.ipc.uda.service.locator.mbean.ServiceLocatorConfig)
     */
    @Override
    protected void configureServiceLocator(ServiceLocatorConfig cfg)
    {
        ServiceLocatorProvider.configureUdaServiceLocator(cfg);
    }
    /* (non-Javadoc)
     * @see com.ipc.uda.service.locator.AbstractServiceLocatorLifecycleTask#getProviderHostnameParam()
     */
    @Override
    protected String getProviderHostnameParam()
    {
        return UDA_PROVIDER_HOSTNAME_PARAM;
    }
    /* (non-Javadoc)
     * @see com.ipc.uda.service.locator.AbstractServiceLocatorLifecycleTask#getProviderPortParam()
     */
    @Override
    protected String getProviderPortParam()
    {
        return UDA_PROVIDER_PORT_PARAM;
    }
    
}
