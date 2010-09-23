/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.locator;



/**
 * 
 * 
 * @author mordarsd
 * 
 */
public class ServiceLocation
{

    private final String hostName;
    private final int port;

    public ServiceLocation(final String hostName, final int port)
    {
        this.hostName = hostName;
        this.port = port;
        if (this.hostName == null)
        {
            throw new IllegalStateException("hostName cannot be null");
        }
    }

    /**
     * @return the hostName
     */
    public String getHostName()
    {
        return this.hostName;
    }

    /**
     * @return the port
     */
    public int getPort()
    {
        return this.port;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;

        int result = 1;

        result = prime * result + this.hostName.hashCode();
        result = prime * result + this.port;

        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof ServiceLocation))
        {
            return false;
        }

        final ServiceLocation other = (ServiceLocation)obj;
        if (!this.hostName.equals(other.hostName))
        {
            return false;
        }
        if (this.port != other.port)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "ServiceLocation [hostName=" + this.hostName + ", port=" + this.port + "]";
    }
}
