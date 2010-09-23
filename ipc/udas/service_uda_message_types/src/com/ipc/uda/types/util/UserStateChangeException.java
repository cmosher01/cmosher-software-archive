/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types.util;


/**
 * @author mordarsd
 *
 */
public final class UserStateChangeException extends Exception
{

    private final String deviceID;
    private final String userAOR;
    private final String state;
    
    public UserStateChangeException(String userAOR, String deviceID, String state, 
                               String message, Throwable causedBy)
    {
        super(message, causedBy);
        
        this.userAOR = userAOR;
        this.deviceID = deviceID;
        this.state = state;
    }

    /**
     * @return the deviceID
     */
    public String getDeviceID()
    {
        return deviceID;
    }

    /**
     * @return the userAOR
     */
    public String getUserAOR()
    {
        return userAOR;
    }

    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    @Override
    public String getMessage() 
    {
        return toString();
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "AgentStateException [deviceID=" + deviceID + ", state=" + state + ", userAOR=" + userAOR + ", causedByMessage="
                + super.getMessage() + "]";
    }

    
    
    
    
}
