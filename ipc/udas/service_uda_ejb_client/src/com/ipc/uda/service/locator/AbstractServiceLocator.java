/**
 * 
 */
package com.ipc.uda.service.locator;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.ipc.uda.service.locator.mbean.ServiceLocatorConfig;
import com.ipc.uda.service.util.jmx.JMXUtil;
import com.ipc.uda.service.util.logging.Log;

/**
 * @author mordarsd
 *
 */
public abstract class AbstractServiceLocator implements ServiceLocator
{
    
    protected ServiceLocatorConfig serviceLocatorCfg;
    protected LocationType locationType = LocationType.LOCAL_API;
    
    protected enum LocationType 
    {
        REMOTE_EJB,
        LOCAL_EJB,
        LOCAL_API,
        MOCK
    }
    

    @Override
    public void configure(ServiceLocatorConfig config)
    {
        if (config.getHostName() == null || config.getHostName().isEmpty())
        {
            this.locationType = LocationType.LOCAL_API;
        } 
        else if ("mock".equalsIgnoreCase(config.getHostName()))
        {
            this.locationType = LocationType.MOCK;
        }
        else
        {
            
            this.serviceLocatorCfg = config;
            
            final String localAddr = JMXUtil.getInstanceListenAddress();
            final int localPort = JMXUtil.getInstanceListenPort();
    
            ServiceLocatorConfig localLocation = null;
            // If the localAddr of this WL instance is actually "localhost",
            // create a ServiceLocation with "localhost" instance of the localAddr passed back from
            // the JMXUtil.getInstanceListenAddress()
            if (isLocalHost(localAddr))
            {
                localLocation = new ServiceLocatorConfig("localhost", localPort);
            }
            else
            {
                localLocation = new ServiceLocatorConfig(localAddr, localPort);
            }
            
            this.locationType = localLocation.equals(config) ? LocationType.LOCAL_EJB : LocationType.REMOTE_EJB;
        }
        // TODO: remove
        System.out.println( getClass().getName() + " is using LocationType: " + this.locationType );
        
    }
    
    private boolean isLocalHost(String instanceAddr)
    {
        boolean local = false;
        try
        {
            final String hostName = extractHostName(instanceAddr);
            
            InetAddress localHost = InetAddress.getLocalHost();
            local = localHost.getCanonicalHostName().equals(hostName);
        }
        catch (UnknownHostException e)
        {
            Log.logger().debug("unknown host"+instanceAddr,e);
        }
        return local;
        
    }
    
    private String extractHostName(String hostAddr)
    {
        String hostName = null;
        final String[] parts = hostAddr.split("/");
        if (parts.length == 2)
        {
            // If there's no hostName, just use the host address
            if (parts[0].isEmpty())
            {
                hostName = parts[1];
            }
            else
            {
                hostName = parts[0];
            }
        }
        
        return hostName;
    }

}
