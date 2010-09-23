/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author mosherc
 *
 */
public class StringsTest
{

    /**
     * Test method for {@link com.ipc.uda.service.util.Strings#splitList(java.lang.String, java.util.Collection)}.
     */
    @Test
    public void testSplitList()
    {
        final String list = "a b,c;d\te\rf\ng  h   i    j,,,,k";

        final List<String> rItem = new ArrayList<String>();
        Strings.splitList(list,rItem);

        assertEquals(11,rItem.size());
        assertEquals("a",rItem.get(0));
        assertEquals("b",rItem.get(1));
        assertEquals("c",rItem.get(2));
        assertEquals("d",rItem.get(3));
        assertEquals("e",rItem.get(4));
        assertEquals("f",rItem.get(5));
        assertEquals("g",rItem.get(6));
        assertEquals("h",rItem.get(7));
        assertEquals("i",rItem.get(8));
        assertEquals("j",rItem.get(9));
        assertEquals("k",rItem.get(10));
    }

    @Test(expected=IllegalStateException.class)
    public void testCannotInstantiate()
    {
        new Strings();
    }
}
