/**
 * 
 */
package com.ipc.uda.service.lifecycle;

import weblogic.application.ApplicationContext;



/**
 * @author mordarsd
 * 
 */
public interface LifecycleTask
{

    /**
	 * 
	 */
    void doStartup(ApplicationContext app) throws LifecycleException;

    /**
	 * 
	 */
    void doShutdown(ApplicationContext app) throws LifecycleException;

}
