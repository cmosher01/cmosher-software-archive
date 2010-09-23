/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.locator.mbean;

/**
 * @author mordarsd
 *
 */
public interface ServiceLocatorConfigMBean
{
    
//    public static enum OperationType 
//    {
//        SET_HOSTNAME,
//        SET_PORT
//    };
    
    public void setHostName(String hostName);
    
    public String getHostName();
    
    public void setPort(int port);
    
    public int getPort();
}
