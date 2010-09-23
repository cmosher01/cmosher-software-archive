/**
 * 
 */
package com.ipc.uda.service.callproc;

import com.ipc.uda.service.callproc.delegate.CallControlListenerDelegate;
import com.ipc.uda.service.callproc.delegate.LineStatusListenerDelegate;
import com.ipc.uda.service.callproc.delegate.LogicalDeviceListenerDelegate;
import com.ipc.uda.service.callproc.delegate.PhysicalDeviceListenerDelegate;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.locator.CtiServiceLocator;
import com.ipc.uda.service.locator.ServiceLocatorProperties;
import com.ipc.uda.service.locator.ServiceLocatorProvider;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.va.cti.ListenerConfig;
import com.ipc.va.cti.ListenerConfigImpl;
import com.ipc.va.cti.ListenerType;
import com.ipc.va.cti.ResultStatusType;
import com.ipc.va.cti.callControl.events.CallControlListener;
import com.ipc.va.cti.lineStatus.events.LineStatusListener;
import com.ipc.va.cti.logicalDevice.AgentIDImpl;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;
import com.ipc.va.cti.monitoring.MonitorFilter;
import com.ipc.va.cti.monitoring.MonitorFilterImpl;
import com.ipc.va.cti.monitoring.MonitorObject;
import com.ipc.va.cti.monitoring.MonitorObjectImpl;
import com.ipc.va.cti.monitoring.MonitorType;
import com.ipc.va.cti.monitoring.services.MonitoringServices;
import com.ipc.va.cti.monitoring.services.extensions.MonitorStartExtensions;
import com.ipc.va.cti.monitoring.services.extensions.MonitorStartExtensionsImpl;
import com.ipc.va.cti.monitoring.services.extensions.MonitorStopExtensions;
import com.ipc.va.cti.monitoring.services.extensions.MonitorStopExtensionsImpl;
import com.ipc.va.cti.monitoring.services.results.MonitorStartResult;
import com.ipc.va.cti.physicalDevice.events.PhysicalDeviceListener;

/**
 * @author mordarsd
 *
 */
public class CtiCallbackHandler
{

    
    private LogicalDeviceListener logicalDeviceListener;
    private PhysicalDeviceListener physicalDeviceListener;
    private CallControlListener callControlListener;
    private LineStatusListener lineStatusListener;
    
    private MonitoringServices monitoringServices;
    private MonitorCrossRefID xrefID;
    private ListenerConfig listenerConfig;
    
    private boolean initialized;

    
    /* (non-Javadoc)
     * @see com.ipc.uda.service.lifecycle.LifecycleTask#doShutdown(weblogic.application.ApplicationContext)
     */
    public void dispose()
    {
        try
        {
            tryStopMonitor();
        }
        catch (Exception e)
        {
            Log.logger().info("error stopping CTI monitor",e);
        }
    }

    /* (non-Javadoc)
     * @see com.ipc.uda.service.lifecycle.LifecycleTask#doStartup(weblogic.application.ApplicationContext)
     */
    public void initialize(final UserContext userCtx)
    {
        if (!initialized)
        {
            Log.logger().info(">>>>> Initializing CtiCallbackHandler for: " + userCtx);
            try
            {
                this.logicalDeviceListener = new LogicalDeviceListenerDelegate();
                this.physicalDeviceListener = new PhysicalDeviceListenerDelegate();
                this.callControlListener = new CallControlListenerDelegate();
                this.lineStatusListener = new LineStatusListenerDelegate();
                this.listenerConfig = new ListenerConfigImpl(null, ListenerType.LOCAL_LISTENER);
                
                
                this.trySetMonitoringServices(userCtx);
                
                this.tryStartMonitor(userCtx);
    
                // Register our Listener Delegates
                this.tryRegisterCallControlListener();
                this.tryRegisterLogicalDeviceListener();
                this.tryRegisterPhysicalDeviceListener();
                this.tryRegisterLineStatusListener();
                
                initialized = true;
                
                Log.logger().info(">>>>> SUCCESSFULLY Initialized CtiCallbackHandler for: " + userCtx);
            }
            catch ( Exception e )
            {
                Log.logger().info(">>>>> ERROR initializing CTI callback handler for: " + userCtx, e);
            }
        } 
        else
        {
            Log.logger().info(">>>>> NOT Initializing CtiCallbackHandler for: " + userCtx + ", already initialized!");
        }
    }


    private void trySetMonitoringServices(final UserContext userCtx) throws Exception
    {
        final CtiServiceLocator ctiLocator = ServiceLocatorProvider.getCtiServiceLocator();
        final ServiceLocatorProperties props = new ServiceLocatorProperties();
        props.setUsingMockObjects(userCtx.isUsingMockObjects());
        
        this.monitoringServices = ctiLocator.getMonitoringServices(props);
    }
    
    private void tryStartMonitor(UserContext userCtx) throws Exception
    {
        //
        // TODO: find out what we need to pass in!
        //
        final MonitorFilter filter = new MonitorFilterImpl(false, false, false, false);
        final MonitorStartExtensions extension = new MonitorStartExtensionsImpl(0, true);
        
        // final MonitorObject monObj = new MonitorObjectImpl(new DeviceIDImpl(deviceID));
        final MonitorObject monObj = new MonitorObjectImpl(new AgentIDImpl(userCtx.getUser().getName()));
        
        MonitorStartResult res = 
            this.monitoringServices.monitorStart(monObj, filter, MonitorType.MONITOR_TYPE_AGENT, extension);
        
        if ((res != null) && (res.getResultStatus() == ResultStatusType.SUCCESS))
        {
            this.xrefID = res.getCrossRefIdentifier();
            
            // Map this id to the UserContext with the UserContextManager
            UserContextManager.getInstance().setUserContext(this.xrefID, userCtx);
            
        }
        else 
        {
            throw new IllegalStateException(
                    "Unable to START MonitoringServices; " + 
                    (res == null ? "MonitorStartResult was null!" : 
                        "ResultStatus = " + res.getResultStatus()));
        }
      
            
    }
    
    private void tryStopMonitor() throws Exception
    {
        if ((this.monitoringServices != null) && (this.xrefID != null)) 
        {
            // Remove this entry from the UserContextManager
            UserContextManager.getInstance().removeContext(this.xrefID);
            
            MonitorStopExtensions stopExtensions = new MonitorStopExtensionsImpl();
            this.monitoringServices.monitorStop(xrefID, stopExtensions);
        } 
        else
        {
            Log.logger().info(
                    "Unable to Stop MonitoringServices.  " +
                    "MonitoringServices and/or MonitorCrossRefID were null" );
        }
    }
    
    private void tryRegisterLogicalDeviceListener() throws Exception
    {
        this.monitoringServices.addLogicalDeviceListener( this.xrefID, this.listenerConfig, this.logicalDeviceListener );
    }
    
    private void tryRegisterPhysicalDeviceListener() throws Exception
    {
        this.monitoringServices.addPhysicalDeviceListener( this.xrefID, this.listenerConfig, this.physicalDeviceListener );
    }

    private void tryRegisterCallControlListener() throws Exception
    {
        this.monitoringServices.addCallControlListener( this.xrefID, this.listenerConfig, this.callControlListener );   
    }
    
    private void tryRegisterLineStatusListener() throws Exception
    {
        this.monitoringServices.addLineStatusListener( this.xrefID, this.listenerConfig, this.lineStatusListener );
    }
}
