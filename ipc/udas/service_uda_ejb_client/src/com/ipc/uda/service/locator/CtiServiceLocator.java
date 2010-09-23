/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.locator;



import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.ipc.uda.service.EJBHomeLocator;
import com.ipc.uda.service.locator.mock.CallControlServicesMock;
import com.ipc.uda.service.locator.mock.LogicalDeviceServicesMock;
import com.ipc.uda.service.locator.mock.MonitoringServicesMock;
import com.ipc.uda.service.locator.mock.PhysicalDeviceServicesMock;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.va.cti.callControl.services.CallControlServices;
import com.ipc.va.cti.callControl.services.ejb.CallControlServicesLocalHome;
import com.ipc.va.cti.callControl.services.ejb.CallControlServicesRemoteHome;
import com.ipc.va.cti.logicalDevice.services.LogicalDeviceServices;
import com.ipc.va.cti.logicalDevice.services.ejb.LogicalDeviceServicesLocalHome;
import com.ipc.va.cti.logicalDevice.services.ejb.LogicalDeviceServicesRemoteHome;
import com.ipc.va.cti.monitoring.services.MonitoringServices;
import com.ipc.va.cti.monitoring.services.ejb.MonitoringServicesLocalHome;
import com.ipc.va.cti.monitoring.services.ejb.MonitoringServicesRemoteHome;
import com.ipc.va.cti.physicalDevice.services.PhysicalDeviceServices;
import com.ipc.va.cti.physicalDevice.services.ejb.PhysicalDeviceServicesLocalHome;
import com.ipc.va.cti.physicalDevice.services.ejb.PhysicalDeviceServicesRemoteHome;



/**
 * 
 * 
 * 
 * @author mordarsd
 * 
 */
public final class CtiServiceLocator extends AbstractServiceLocator
{

    
    private static final String BASE_CTX = "com.ipc.va.cti";
    private static final String LOCAL_CTX = BASE_CTX + ".local.session.";
    private static final String REMOTE_CTX = BASE_CTX + ".remote.session.";
    
    private static enum ServiceName
    {
        CallControlServices,
        LogicalDeviceServices,
        PhysicalDeviceServices,
        MonitoringServices
    }
  
    private static final Map<ServiceName, String> SVC_NAME_IMPL_MAP = new HashMap<ServiceName, String>(4);

//    private static final Map<ServiceName, ServiceImpls<?>> map = new HashMap<ServiceName, ServiceImpls<?>>();
    private static final Map<ServiceName, ServiceImpls> IMPLS_MAP = new HashMap<ServiceName, ServiceImpls>();
        
    static
    {
        SVC_NAME_IMPL_MAP.put( ServiceName.CallControlServices, 
                                "com.ipc.va.cti.callControl.services.CallControlServicesImpl" );
        SVC_NAME_IMPL_MAP.put( ServiceName.LogicalDeviceServices, 
                                "com.ipc.va.cti.logicalDevice.services.LogicalDeviceServicesImpl" );
        SVC_NAME_IMPL_MAP.put( ServiceName.PhysicalDeviceServices, 
                                "com.ipc.va.cti.physicalDevice.services.PhysicalDeviceServicesImpl" );
        SVC_NAME_IMPL_MAP.put( ServiceName.MonitoringServices, 
                                "com.ipc.va.cti.monitoring.services.MonitoringServicesImpl" );
            
        
        IMPLS_MAP.put(ServiceName.CallControlServices, new ServiceImpls());//<CallControlServices>());
        IMPLS_MAP.put(ServiceName.LogicalDeviceServices, new ServiceImpls());//<LogicalDeviceServices>());
        IMPLS_MAP.put(ServiceName.PhysicalDeviceServices, new ServiceImpls());//<PhysicalDeviceServices>());
        IMPLS_MAP.put(ServiceName.MonitoringServices, new ServiceImpls());//<MonitoringServices>());
    }

    
    
    public synchronized CallControlServices getCallControlService() throws ServiceLocatorException
    {
        return this.getCallControlService(null);
    }
    
    /**
     * ServiceLocatorProperties can be used when an API Client may require a certain interface instance  (ie: mock objects) 
     * 
     * @return
     */
    public synchronized CallControlServices getCallControlService(ServiceLocatorProperties props) throws ServiceLocatorException
    {
        return (CallControlServices)locate(ServiceName.CallControlServices, props);
    }

    public synchronized LogicalDeviceServices getLogicalDeviceServices() throws ServiceLocatorException
    {
        return this.getLogicalDeviceServices(null);
    }
    
    /**
     * 
     * @return
     */
    public synchronized LogicalDeviceServices getLogicalDeviceServices(ServiceLocatorProperties props) throws ServiceLocatorException
    {
        return (LogicalDeviceServices)locate(ServiceName.LogicalDeviceServices, props);
    }

    public synchronized PhysicalDeviceServices getPhysicalDeviceServices() throws ServiceLocatorException
    {
        return this.getPhysicalDeviceServices(null);
    }
    
    /**
     * 
     * @return
     */
    public synchronized PhysicalDeviceServices getPhysicalDeviceServices(ServiceLocatorProperties props) throws ServiceLocatorException
    {
        return (PhysicalDeviceServices)locate(ServiceName.PhysicalDeviceServices, props); 
    }

    public synchronized MonitoringServices getMonitoringServices() throws ServiceLocatorException
    {
        return this.getMonitoringServices(null);
    }
    
    /**
     * 
     * @return
     */
    public synchronized MonitoringServices getMonitoringServices(ServiceLocatorProperties props) throws ServiceLocatorException
    {
        return (MonitoringServices)locate(ServiceName.MonitoringServices, props);
    }

    private Object locate(final ServiceName serviceName, ServiceLocatorProperties props)
    {
        Object service = null;
        LocationType locType = this.getLocationType(props);
        
//        ServiceImpls<?> impls = map.get(serviceName);
        ServiceImpls impls = IMPLS_MAP.get(serviceName);
        
        switch( locType )
        {
         
            case LOCAL_API:
                
                if ((service = impls.localApi) == null)
                {
                    final String impl = SVC_NAME_IMPL_MAP.get( serviceName );
                    try {
                        Class<?> clazz = Class.forName( impl );
                        if (clazz != null)
                        {
                            Method m = clazz.getMethod("getInstance");
                            service = m.invoke( null );
                     
                            impls.localApi = service;
                            //System.out.printf( ">>>>> %s is using :%s", serviceName, service );
                        }
                    } 
                    catch ( Exception e ) 
                    {
                        Log.logger().debug("error localing service: "+serviceName,e);
                    }
                }
                break;
            
            case LOCAL_EJB:
            case REMOTE_EJB:
                final boolean local = (locType == LocationType.LOCAL_EJB);
                try
                {
                    final Object home = getHome(serviceName, local);
                    if (home != null)
                    {
                        service = getService(serviceName, local, home);
                    }
                }
                catch (final Throwable e)
                {
                    throw new ServiceLocatorException("Error locating service: " + serviceName,e);
                }
        
                if (service == null)
                {
                    throw new ServiceLocatorException("Unable to locate " + (local ? "local" : "remote") + " home for " + serviceName);
                }
                break;
                
            case MOCK:
                if ((service = impls.mock) == null)
                {
                    switch(serviceName)
                    {
                        case CallControlServices:
                            service = new CallControlServicesMock();
                            break;
                            
                        case LogicalDeviceServices:
                            service = new LogicalDeviceServicesMock();
                            break;
                            
                        case PhysicalDeviceServices:
                            service = new PhysicalDeviceServicesMock();
                            break;
                            
                        case MonitoringServices:
                            service = new MonitoringServicesMock();
                            break;
                            
                    }
                    impls.mock = service;
                }
                break;
        }
        
        
        
        return service;
    }

    private Object getService(final ServiceName serviceName, final boolean local, final Object home) throws CreateException, RemoteException
    {
        Object service;

        if (ServiceName.CallControlServices.equals(serviceName))
        {
            service = (local) ? ((CallControlServicesLocalHome)home).create() : ((CallControlServicesRemoteHome)home)
                    .create();

        }
        else if (ServiceName.LogicalDeviceServices.equals(serviceName))
        {
            service = (local) ? ((LogicalDeviceServicesLocalHome)home).create()
                    : ((LogicalDeviceServicesRemoteHome)home).create();

        }
        else if (ServiceName.PhysicalDeviceServices.equals(serviceName))
        {
            service = (local) ? ((PhysicalDeviceServicesLocalHome)home).create()
                    : ((PhysicalDeviceServicesRemoteHome)home).create();

        }
        else if (ServiceName.MonitoringServices.equals(serviceName))
        {
            service = (local) ? ((MonitoringServicesLocalHome)home).create() : ((MonitoringServicesRemoteHome)home)
                    .create();

        }
        else
        {
            // unknown service name
            service = null;
        }

        return service;
    }

    private Object getHome(final ServiceName serviceName, final boolean local) throws NamingException
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


    private LocationType getLocationType(ServiceLocatorProperties props)
    {
        if ((props != null) && (props.isUsingMockObjects()))
        {
            return LocationType.MOCK;
        }
        return this.locationType;
    }

//    private static final class ServiceImpls<T>
//    {
//        
//        private Map<LocationType, T> locMap;
//        
//        void set(LocationType locType, T t)
//        {
//            locMap.put(locType, t);
//        }
//        
//        public T get(LocationType locType)
//        {
//            return locMap.get(locType);
//        }
//    }

    private static final class ServiceImpls
    {
        public Object localApi;
        public Object mock;
    }

    
}
