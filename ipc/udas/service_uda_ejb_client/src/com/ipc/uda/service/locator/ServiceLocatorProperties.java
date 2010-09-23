/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.locator;

/**
 * @author mordarsd
 *
 */
public class ServiceLocatorProperties
{

    private boolean usingMockObjects;
    
    public ServiceLocatorProperties()
    {
    }

    /**
     * @return the usingMockObjects
     */
    public boolean isUsingMockObjects()
    {
        return usingMockObjects;
    }

    /**
     * @param usingMockObjects the usingMockObjects to set
     */
    public void setUsingMockObjects(boolean usingMockObjects)
    {
        this.usingMockObjects = usingMockObjects;
    }


    
    
    
}
