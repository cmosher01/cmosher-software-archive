/**
 * 
 */
package com.ipc.uda.service.context;

import javax.management.MBeanServer;

import weblogic.application.ApplicationContext;

import com.ipc.uda.service.context.mbean.UserContextManagerState;
import com.ipc.uda.service.context.mbean.UserContextManagerStateHelper;
import com.ipc.uda.service.context.mbean.UserContextManagerStateMBean;
import com.ipc.uda.service.lifecycle.LifecycleException;
import com.ipc.uda.service.lifecycle.LifecycleTask;
import com.ipc.uda.service.util.jmx.JMXUtil;

/**
 * @author mordarsd
 *
 */
public class UserContextManagerLifecycleTask implements LifecycleTask
{

//    private ObjectName userCtxMgrStateObjectName;
//    
//    public UserContextManagerLifecycleTask()
//    {
//        try
//        {
//            this.userCtxMgrStateObjectName = 
//                ObjectNameFactory.create(
//                        "ipc", "uda", 
//                        UserContextManagerState.class.getSimpleName());
//        }
//        catch (MalformedObjectNameException e)
//        {
//            Log.logger().info(
//                    "Unable to create ObjectName for MBean " + 
//                    UserContextManagerStateMBean.class.getSimpleName(), e);
//        }
//    }
    
    /* (non-Javadoc)
     * @see com.ipc.uda.service.lifecycle.LifecycleTask#doShutdown(weblogic.application.ApplicationContext)
     */
    @Override
    public void doShutdown(ApplicationContext app) throws LifecycleException
    {
        try
        {
            final MBeanServer server = JMXUtil.getLocalRuntimeMBeanServer();
            if (server.isRegistered(UserContextManagerStateHelper.userCtxMgrStateObjectName))
            {
                server.unregisterMBean(UserContextManagerStateHelper.userCtxMgrStateObjectName);
            }

        }
        catch (Exception e)
        {
            throw new LifecycleException(
                    "Unable to deregister MBean: " + 
                    UserContextManagerStateMBean.class.getSimpleName(), e);
        }
        
    }

    /* (non-Javadoc)
     * @see com.ipc.uda.service.lifecycle.LifecycleTask#doStartup(weblogic.application.ApplicationContext)
     */
    @Override
    public void doStartup(ApplicationContext app) throws LifecycleException
    {
        try
        {
            final MBeanServer server = JMXUtil.getLocalRuntimeMBeanServer();
            if (server.isRegistered(UserContextManagerStateHelper.userCtxMgrStateObjectName))
            {
                server.unregisterMBean(UserContextManagerStateHelper.userCtxMgrStateObjectName);
            }

            server.registerMBean(new UserContextManagerState(), UserContextManagerStateHelper.userCtxMgrStateObjectName);
            
        }
        catch (Exception e)
        {
            throw new LifecycleException(
                    "Unable to register MBean: " + 
                    UserContextManagerStateMBean.class.getSimpleName(), e);
        }
    }

}
