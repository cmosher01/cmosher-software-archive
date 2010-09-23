/**
 * 
 */
package com.ipc.uda.service.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author mosherc
 */
public class UdaEnvironmentExceptionTest
{

    /**
     * Test method for {@link com.ipc.uda.service.util.UdaEnvironmentException#UdaEnvironmentException()}.
     */
    @Test
    public void testUdaEnvironmentException()
    {
        final Throwable t = new UdaEnvironmentException();
        assertNull(t.getMessage());
        assertNull(t.getCause());
    }

    /**
     * Test method for {@link com.ipc.uda.service.util.UdaEnvironmentException#UdaEnvironmentException(java.lang.String, java.lang.Throwable)}.
     */
    @Test
    public void testUdaEnvironmentExceptionStringThrowable()
    {
        final Throwable cause = new Throwable();
        final Throwable t = new UdaEnvironmentException("message",cause);
        assertEquals("message",t.getMessage());
        assertEquals(cause,t.getCause());
    }

    /**
     * Test method for {@link com.ipc.uda.service.util.UdaEnvironmentException#UdaEnvironmentException(java.lang.String)}.
     */
    @Test
    public void testUdaEnvironmentExceptionString()
    {
        final Throwable t = new UdaEnvironmentException("message");
        assertEquals("message",t.getMessage());
        assertNull(t.getCause());
    }

    /**
     * Test method for {@link com.ipc.uda.service.util.UdaEnvironmentException#UdaEnvironmentException(java.lang.Throwable)}.
     */
    @Test
    public void testUdaEnvironmentExceptionThrowable()
    {
        final Throwable cause = new Throwable("message");
        final Throwable t = new UdaEnvironmentException(cause);
        assertEquals(Throwable.class.getName()+": message",t.getMessage());
        assertEquals(cause,t.getCause());
    }
}
