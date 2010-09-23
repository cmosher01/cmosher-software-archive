/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author mosherc
 *
 */
public class ReconnectionTimerTest
{
    @Test
    public void connectActive()
    {
        final ReconnectionTimer timer = new ReconnectionTimer(2000);
        timer.connect();
        assertTrue(timer.isActive());
    }

    @Test
    public void timeoutDeactivates() throws InterruptedException
    {
        final ReconnectionTimer timer = new ReconnectionTimer(1000);
        timer.connect();
        timer.disconnect();
        Thread.sleep(1500);
        assertFalse(timer.isActive());
    }

    @Test
    public void reconnectActivates() throws InterruptedException
    {
        final ReconnectionTimer timer = new ReconnectionTimer(1000);
        timer.connect();
        timer.disconnect();
        timer.connect();
        Thread.sleep(1500);
        assertTrue(timer.isActive());
    }
}
