/**
 * 
 */
package com.ipc.uda.service.context;

import java.io.Serializable;

import com.ipc.uda.service.util.UdaPrincipal;

/**
 * @author mordarsd
 *
 */
public final class UserID implements Serializable
{

    private final UdaPrincipal principal;
    private final String deviceID;
    
    public UserID(UdaPrincipal principal, String deviceID)
    {
        this.principal = principal;
        this.deviceID = deviceID;
    }

    public UdaPrincipal getPrincipal()
    {
        return principal;
    }

    public String getDeviceID()
    {
        return deviceID;
    }
    
    

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deviceID == null) ? 0 : deviceID.hashCode());
        result = prime * result + ((principal == null) ? 0 : principal.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof UserID))
        {
            return false;
        }
        UserID other = (UserID)obj;
        if (deviceID == null)
        {
            if (other.deviceID != null)
            {
                return false;
            }
        }
        else if (!deviceID.equals(other.deviceID))
        {
            return false;
        }
        if (principal == null)
        {
            if (other.principal != null)
            {
                return false;
            }
        }
        else if (!principal.equals(other.principal))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "UserID [deviceID=" + deviceID + ", principal=" + principal + "]";
    }
    
    
    
}
