/**
 * 
 */
package com.ipc.uda.service.context.mbean;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.util.jmx.JMXUtil;
import com.ipc.uda.service.util.jmx.ObjectNameFactory;
import com.ipc.uda.service.util.logging.Log;

/**
 * @author mordarsd
 *
 */
public final class UserContextManagerStateHelper
{

    public static ObjectName userCtxMgrStateObjectName;
    
    private static final String[] ADD_USER_ID_SIG = new String[] { UserID.class.getName() };
    private static final String ADD_USER_ID_NAME = "addUserID";
    private static final String[] REMOVE_USER_ID_SIG = new String[] { UserID.class.getName() };
    private static final String REMOVE_USER_ID_NAME = "removeUserID";
    
    static
    {
        try
        {
            userCtxMgrStateObjectName = 
                ObjectNameFactory.create(
                        "ipc", "uda", 
                        UserContextManagerStateMBean.class.getSimpleName());
        }
        catch (MalformedObjectNameException e)
        {
            Log.logger().info(
                    "Unable to create ObjectName for MBean " + 
                    UserContextManagerStateMBean.class.getSimpleName(), e);
        }
    }
    
    /**
     * 
     * @param userID
     */
    public static void addUserID(final UserID userID)
    {
        try
        {
            final MBeanServer server = JMXUtil.getLocalRuntimeMBeanServer();
            server.invoke(userCtxMgrStateObjectName, ADD_USER_ID_NAME, new Object[]{userID}, ADD_USER_ID_SIG);
        }
        catch (Exception e)
        {
            Log.logger().info(
                    "Unable to invoke addUserID for UserID: [" + userID + "] on MBean " + 
                    UserContextManagerStateMBean.class.getSimpleName(), e);
        }
     
    }
    
    /**
     * 
     * @param userID
     */
    public static void removeUserID(final UserID userID)
    {
        try
        {
            final MBeanServer server = JMXUtil.getLocalRuntimeMBeanServer();
            server.invoke(userCtxMgrStateObjectName, REMOVE_USER_ID_NAME, new Object[]{userID}, REMOVE_USER_ID_SIG);
        }
        catch (Exception e)
        {
            Log.logger().info(
                    "Unable to invoke removeUserID for UserID: [" + userID + "] on MBean " + 
                    UserContextManagerStateMBean.class.getSimpleName(), e);
        }
    }

    
    
}
