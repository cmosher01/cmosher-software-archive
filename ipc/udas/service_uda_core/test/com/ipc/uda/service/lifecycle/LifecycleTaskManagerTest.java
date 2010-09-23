package com.ipc.uda.service.lifecycle;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.naming.Context;
import javax.security.jacc.PolicyConfiguration;

import org.junit.Test;

import weblogic.application.ApplicationContext;
import weblogic.application.ApplicationDescriptor;
import weblogic.application.library.LibraryManager;
import weblogic.application.library.LoggableLibraryProcessingException;
import weblogic.application.utils.XMLWriter;
import weblogic.j2ee.descriptor.ApplicationBean;
import weblogic.management.configuration.AppDeploymentMBean;
import weblogic.management.configuration.ApplicationMBean;
import weblogic.utils.classloaders.ClassFinder;
import weblogic.utils.classloaders.GenericClassLoader;

public class LifecycleTaskManagerTest
{
    private static class NullContext implements ApplicationContext
    {

        @Override
        public void addJACCPolicyConfiguration(PolicyConfiguration arg0)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void addLibraryManager(String arg0, LibraryManager arg1)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public GenericClassLoader getAppClassLoader()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public AppDeploymentMBean getAppDeploymentMBean()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getApplicationId()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ApplicationMBean getApplicationMBean()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getApplicationName()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getApplicationParameter(String arg0)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getApplicationSecurityRealmName()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public InputStream getElement(String arg0) throws IOException
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Context getEnvContext()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void writeDiagnosticImage(XMLWriter arg0)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void addClassFinder(ClassFinder arg0)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public ApplicationBean getApplicationDD()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ApplicationDescriptor getApplicationDescriptor()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Map getContextRootOverrideMap()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getRefappName()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getRefappUri()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void notifyDescriptorUpdate() throws LoggableLibraryProcessingException
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void registerLink(File arg0) throws IOException
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void registerLink(String arg0, File arg1) throws IOException
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void setContextRootOverrideMap(Map arg0)
        {
            // TODO Auto-generated method stub

        }

    }

    static class MockTask implements LifecycleTask
    {
        boolean started;
        boolean stopped;
        @Override
        public void doStartup(ApplicationContext app)
        {
            this.started = true;
        }
        @Override
        public void doShutdown(ApplicationContext app)
        {
            this.stopped = true;
        }
    }



    @Test
    public void nominal() throws LifecycleException
    {
        final LifecycleTaskManager mgr = new LifecycleTaskManager();
        final MockTask task1 = new MockTask();
        mgr.add(task1);
        final MockTask task2 = new MockTask();
        mgr.add(task2);
        final MockTask task3 = new MockTask();
        mgr.add(task3);
        mgr.remove(task3);

        mgr.performStartupTasks(new NullContext());

        assertTrue(task1.started);
        assertFalse(task1.stopped);

        assertTrue(task2.started);
        assertFalse(task2.stopped);

        assertFalse(task3.started);
        assertFalse(task3.stopped);

        mgr.performShutdownTasks(new NullContext());

        assertTrue(task1.started);
        assertTrue(task1.stopped);

        assertTrue(task2.started);
        assertTrue(task2.stopped);

        assertFalse(task3.started);
        assertFalse(task3.stopped);
    }

    static class TaskThatThrowsDuringShutdown implements LifecycleTask
    {
        boolean started;
        boolean stopped;
        @Override
        public void doStartup(ApplicationContext app)
        {
            this.started = true;
        }
        @Override
        public void doShutdown(ApplicationContext app)
        {
            this.stopped = true;
            throw new IllegalStateException();
        }
    }

    @Test
    public void exceptionDuringShutdown() throws LifecycleException
    {
        final LifecycleTaskManager mgr = new LifecycleTaskManager();
        final MockTask task1 = new MockTask();
        mgr.add(task1);
        final TaskThatThrowsDuringShutdown task2 = new TaskThatThrowsDuringShutdown();
        mgr.add(task2);
        final MockTask task3 = new MockTask();
        mgr.add(task3);

        mgr.performStartupTasks(new NullContext());
        mgr.performShutdownTasks(new NullContext());

        // even though task 2 throws an (unchecked) exception during shutdown,
        // following tasks should still get shutdown
        assertTrue(task1.stopped);
        assertTrue(task2.stopped);
        assertTrue(task3.stopped);
    }
}
