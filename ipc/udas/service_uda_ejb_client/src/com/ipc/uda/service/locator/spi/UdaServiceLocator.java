/**
 * 
 */
package com.ipc.uda.service.locator.spi;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.ipc.uda.service.EJBHomeLocator;
import com.ipc.uda.service.locator.AbstractServiceLocator;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.va.cti.callControl.events.CallControlListener;
import com.ipc.va.cti.lineStatus.events.LineStatusListener;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener;
import com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener;

/**
 * @author mordarsd
 *
 */
public class UdaServiceLocator extends AbstractServiceLocator
{
    
    private static final String BASE_CTX = "uda.ejb";
    private static final String LOCAL_CTX = BASE_CTX + ".local.session.";
    private static final String REMOTE_CTX = BASE_CTX + ".remote.session.";
    
    private static final Map<ListenerName, String> LISTENER_NAME_IMPL_MAP = new HashMap<ListenerName, String>(3);
    
    private CallControlListener callControlListener;
    private LogicalDeviceListener logicalDeviceListener;
    private PhysicalDeviceListener physicalDeviceListener;
    private LineStatusListener lineStatusListener;
    
    private enum ListenerName
    {
        CallControl,
        LogicalDevice,
        PhysicalDevice,
        LineStatus
    }
    
    static
    {
        LISTENER_NAME_IMPL_MAP.put(ListenerName.CallControl, "com.ipc.uda.service.callproc.CallControlListenerImpl");
        LISTENER_NAME_IMPL_MAP.put(ListenerName.LogicalDevice, "com.ipc.uda.service.callproc.LogicalDeviceListenerImpl");
        LISTENER_NAME_IMPL_MAP.put(ListenerName.PhysicalDevice, "com.ipc.uda.service.callproc.PhysicalDeviceListenerImpl");
        LISTENER_NAME_IMPL_MAP.put(ListenerName.LineStatus, "com.ipc.uda.service.callproc.LineStatusListenerImpl");
    }
    
    
    public synchronized CallControlListener getCallControlListener()
    {
        if (this.callControlListener == null)
        {
            this.callControlListener = (CallControlListener)this.locate(ListenerName.CallControl);
        }
        return this.callControlListener;
    }
    
    public synchronized LogicalDeviceListener getLogicalDeviceListener()
    {
        if (this.logicalDeviceListener == null)
        {
            this.logicalDeviceListener = (LogicalDeviceListener)this.locate(ListenerName.LogicalDevice);
        }
        return this.logicalDeviceListener;
    }
    
    public synchronized PhysicalDeviceListener getPhysicalDeviceListener()
    {
        if (this.physicalDeviceListener == null)
        {
            this.physicalDeviceListener = (PhysicalDeviceListener)this.locate(ListenerName.PhysicalDevice);
        }
        return this.physicalDeviceListener;
    }

    public synchronized LineStatusListener getLineStatusListener()
    {
        if (this.lineStatusListener == null)
        {
            this.lineStatusListener = (LineStatusListener)this.locate(ListenerName.LineStatus);
        }
        return this.lineStatusListener;
    }
    
    private Object locate(final ListenerName serviceName)
    {
        Object listener = null;
        
        switch( this.locationType )
        {
         
            case LOCAL_API:
                final String impl = LISTENER_NAME_IMPL_MAP.get( serviceName );
                try {
                    Class<?> clazz = Class.forName( impl );
                    if (clazz != null)
                    {
                        Method m = clazz.getMethod("getInstance");
                        listener = m.invoke( null );
                        System.out.printf( ">>>>> %s is using :%s\n", serviceName, listener );
                    }
                } 
                catch ( Exception e ) 
                {
                    Log.logger().info("error locaing service "+serviceName,e);
                }
                break;
            
            case LOCAL_EJB:
            case REMOTE_EJB:
//                final boolean local = (this.locationType == LocationType.LOCAL);
//        
//                
//                try
//                {
//                    final Object home = getHome(serviceName, local);
//                    if (home != null)
//                    {
//                        listener = getListenerEjb(serviceName, local, home);
//                    }
//                }
//                catch (final Throwable e)
//                {
//                    throw new ServiceLocatorException("Error locating service: " + serviceName,e);
//                }
//        
//                if (listener == null)
//                {
//                    throw new ServiceLocatorException("Unable to locate " + (local ? "local" : "remote") + " home for " + serviceName);
//                }
//                break;
                
                // Currently, only local API calls are supported
                throw new UnsupportedOperationException(this.locationType + " is not supported!");
        }
        
        return listener;
    }

    private Object getListenerEjb(final ListenerName serviceName, final boolean local, final Object home) throws CreateException, RemoteException
    {
        Object listener = null;

        if (ListenerName.CallControl.equals(serviceName))
        {
        }
        else if (ListenerName.LogicalDevice.equals(serviceName))
        {
        }
        else if (ListenerName.PhysicalDevice.equals(serviceName))
        {
        }
        else
        {
            // unknown listener name

        }

        return listener;
    }

    private Object getHome(final ListenerName serviceName, final boolean local) throws NamingException
    {
        Object home;

        if (local)
        {
            home = EJBHomeLocator.getInstance().getLocalHome(LOCAL_CTX + serviceName);

        }
        else
        {
            home = EJBHomeLocator.getInstance().getRemoteHome(REMOTE_CTX + serviceName, this.serviceLocatorCfg);
        }

        return home;
    }

    
}
