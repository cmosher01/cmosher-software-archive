/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ipc.uda.service.callproc.LineStatusListenerImpl;

/**
 * @author mosherc
 *
 */
public class LineStatusListenerImplTest
{
    @Test
    public void testAor()
    {
        assertEquals("1234",LineStatusListenerImpl.parseCtiAor("1234"));
    }
    @Test
    public void testSipAor()
    {
        assertEquals("1234",LineStatusListenerImpl.parseCtiAor("sip:1234"));
    }
    @Test
    public void testAorIP()
    {
        assertEquals("1234",LineStatusListenerImpl.parseCtiAor("1234@10.204.1.1"));
    }
    @Test
    public void testSipAorIP()
    {
        assertEquals("1234",LineStatusListenerImpl.parseCtiAor("sip:1234@10.204.1.1"));
    }
}
