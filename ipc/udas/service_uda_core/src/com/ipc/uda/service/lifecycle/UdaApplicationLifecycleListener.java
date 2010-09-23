/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.lifecycle;



import java.util.ArrayList;
import java.util.Collection;

import com.ipc.uda.service.util.Classes;

import weblogic.application.ApplicationException;
import weblogic.application.ApplicationLifecycleEvent;
import weblogic.application.ApplicationLifecycleListener;



/**
 * <p>
 * Handles tasks that are performed at startup and shutdown of the UDAS application (on the server).
 * WebLogic will call our {@link UdaApplicationLifecycleListener#postStart(ApplicationLifecycleEvent) postStart}
 * and {@link UdaApplicationLifecycleListener#preStop(ApplicationLifecycleEvent) preStop} methods when the
 * UDAS application is started up or shut down (respectively).
 * </p>
 * <p>
 * Any exceptions during this process will throw an {@link ApplicationException} back to WebLogic.
 * </p>
 * @author mordarsd
 * @author mosherc
 * 
 */
public class UdaApplicationLifecycleListener extends ApplicationLifecycleListener
{
    public static final String LIFECYCLE_TASK_CLASS_LIST_PARAMETER = "uda.lifecycle.tasks";

    private static final LifecycleTaskManager manager = new LifecycleTaskManager();



    @Override
    public void postStart(final ApplicationLifecycleEvent event) throws ApplicationException
    {
        registerLifecycleTasks(event);

        try
        {
            UdaApplicationLifecycleListener.manager.performStartupTasks(event.getApplicationContext());
        }
        catch (final Throwable e)
        {
            throw new ApplicationException(e);
        }

        super.postStart(event);
    }

    @Override
    public void preStop(ApplicationLifecycleEvent event) throws ApplicationException
    {
        UdaApplicationLifecycleListener.manager.performShutdownTasks(event.getApplicationContext());

        super.preStop(event);
    }



    private void registerLifecycleTasks(final ApplicationLifecycleEvent event) throws ApplicationException
    {
        final String lifecycleTaskList = event.getApplicationContext().getApplicationParameter(LIFECYCLE_TASK_CLASS_LIST_PARAMETER);

        final Collection<LifecycleTask> rTasks = new ArrayList<LifecycleTask>();
        try
        {
            Classes.<LifecycleTask>createInstancesByClassName(lifecycleTaskList,LifecycleTask.class,rTasks);
        }
        catch (final Throwable e)
        {
            throw new ApplicationException(e);
        }



        for (final LifecycleTask task : rTasks)
        {
            UdaApplicationLifecycleListener.manager.add(task);
        }
    }
}
