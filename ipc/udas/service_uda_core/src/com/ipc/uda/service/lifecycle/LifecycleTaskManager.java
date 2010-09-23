package com.ipc.uda.service.lifecycle;



import weblogic.application.ApplicationContext;

import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.service.util.Registry;



/**
 * Manages application life-cycle tasks ({@link LifecycleTask}s).
 * Provides methods to add and remove tasks, as well as
 * methods to perform all startup tasks ({@link LifecycleTask#doStartup(ApplicationContext) doStartup})
 * and all shutdown tasks ({@link LifecycleTask#doShutdown(ApplicationContext) doShutdown}).
 * @author mordarsd
 * @author mosherc
 */
public class LifecycleTaskManager
{
    private final Registry<LifecycleTask> registry = new Registry<LifecycleTask>();



    /**
     * Adds the given {@link LifecycleTask} to this manager.
     * @param lifecycleTask the task to add
     */
    public void add(final LifecycleTask lifecycleTask)
    {
        this.registry.register(lifecycleTask);
    }

    /**
     * Removes the given {@link LifecycleTask} from this manager.
     * If the given {@link LifecycleTask} is not in this manager, then
     * this method does nothing.
     * @param lifecycleTask the task to remove
     */
    public void remove(final LifecycleTask lifecycleTask)
    {
        this.registry.unregister(lifecycleTask);
    }



    /**
     * Calls {@link LifecycleTask#doStartup(ApplicationContext) doStartup} on each
     * {@link LifecycleTask} in this manager. If any task throws an exception, no further tasks
     * are run, and the exception is thrown.
     * @param app application context to pass to {@link LifecycleTask#doStartup(ApplicationContext) doStartup}
     * @throws LifecycleException if an exception occurs during any call to {@link LifecycleTask#doStartup(ApplicationContext) doStartup}
     */
    public void performStartupTasks(final ApplicationContext app) throws LifecycleException
    {
        for (final LifecycleTask task : this.registry)
        {
            task.doStartup(app);
        }
    }

    /**
     * Calls {@link LifecycleTask#doShutdown(ApplicationContext) doShutdown} on each
     * {@link LifecycleTask} in this manager. If any task throws an exception, it
     * is logged, and then ignored. That is, this method will continue calling the
     * remaining tasks.
     * @param app application context to pass to {@link LifecycleTask#doStartup(ApplicationContext) doStartup}
     */
    public void performShutdownTasks(final ApplicationContext app)
    {
        for (final LifecycleTask task : this.registry)
        {
            try
            {
                task.doShutdown(app);
            }
            catch (final Throwable e)
            {
                Log.logger().debug("Exception occurred while running a shutdown task; ignoring it.",e);
                // Ignore the exception (just log it), because we are just
                // shutting down anyway. And we want to keep trying to run
                // any further shutdown tasks.
            }
        }
    }
}
