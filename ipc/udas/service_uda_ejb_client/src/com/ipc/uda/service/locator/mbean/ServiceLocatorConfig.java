/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.locator.mbean;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

/**
 * @author mordarsd
 *
 */
public final class ServiceLocatorConfig extends NotificationBroadcasterSupport implements ServiceLocatorConfigMBean
{
    
    private String hostName;
    private int port;
    
    public ServiceLocatorConfig()
    {
        this("", 0);
    }
    
    public ServiceLocatorConfig(String hostName, int port)
    {
        this.hostName = hostName;
        this.port = port;
    }
    
    @Override
    public String getHostName()
    {
        return this.hostName;
    }
    
    @Override
    public int getPort()
    {
        return this.port;
    }
    
    @Override
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
        this.sendNotification();
    }
    
    @Override
    public void setPort(int port)
    {
        this.port = port;
        this.sendNotification();
    }
    
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hostName == null) ? 0 : hostName.hashCode());
        result = prime * result + port;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceLocatorConfig other = (ServiceLocatorConfig)obj;
        if (hostName == null)
        {
            if (other.hostName != null)
                return false;
        }
        else if (!hostName.equals(other.hostName))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "ServiceLocatorConfig [hostName=" + hostName + ", port=" + port + "]";
    }

    private void sendNotification()
    {
        Notification notif = new Notification(null, this, 0);
        notif.setUserData(this);
        this.sendNotification(notif);   
    }
    
    
}
