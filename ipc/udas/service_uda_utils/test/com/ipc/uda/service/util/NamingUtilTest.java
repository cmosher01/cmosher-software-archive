/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.util;

import static org.junit.Assert.*;

import javax.naming.Context;
import javax.naming.NamingException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mosherc
 */
public class NamingUtilTest
{
    private final Mockery mockery = new JUnit4Mockery();
    private Context ctxMock;

    @Test(expected=RuntimeException.class)
    public void testCannotInstantiate()
    {
        new NamingUtil();
    }

    @Before
    public void setUp()
    {
        this.ctxMock = this.mockery.mock(Context.class);
    }

    @Test
    public void testCloseContextNominal() throws NamingException
    {
        this.mockery.checking(new Expectations()
        {{
            oneOf (ctxMock).close();
        }});
        NamingUtil.closeContext(this.ctxMock);
    }

    @Test
    public void testCloseContextNull()
    {
        NamingUtil.closeContext(null);
    }

    @Test
    public void testCloseContextException() throws NamingException
    {
        this.mockery.checking(new Expectations()
        {{
            oneOf (ctxMock).close(); will(throwException(new NamingException()));
        }});
        NamingUtil.closeContext(this.ctxMock);
    }
}
