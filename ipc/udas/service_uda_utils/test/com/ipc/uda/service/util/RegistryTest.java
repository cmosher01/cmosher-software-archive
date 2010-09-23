package com.ipc.uda.service.util;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class RegistryTest
{

    @Test
    public void newRegistryIsEmpty()
    {
        final Registry<String> sr = new Registry<String>();
        assertEmptyIteration(sr);
    }

    private void assertEmptyIteration(final Iterable<?> it)
    {
        final Iterator<?> iterator = it.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void registerNominal()
    {
        final String TEST_STRING = "registrant";
        final Registry<String> sr = new Registry<String>();
        sr.register(TEST_STRING);

        final Iterator<String> iterator = sr.iterator();

        assertTrue(iterator.hasNext());
        final String got = iterator.next();
        assertEquals(TEST_STRING,got);

        assertFalse(iterator.hasNext());
    }

    @Test
    public void registerMultiple()
    {
        final Registry<String> sr = new Registry<String>();
        final String TEST_STRING1 = "registrant1";
        sr.register(TEST_STRING1);
        final String TEST_STRING2 = "registrant2";
        sr.register(TEST_STRING2);

        final SortedSet<String> got = new TreeSet<String>();
        for (final String s : sr)
        {
            got.add(s);
        }

        final Iterator<String> iterator = got.iterator();

        assertTrue(iterator.hasNext());
        final String got1 = iterator.next();
        assertEquals(TEST_STRING1,got1);

        assertTrue(iterator.hasNext());
        final String got2 = iterator.next();
        assertEquals(TEST_STRING2,got2);

        assertFalse(iterator.hasNext());
    }

    @Test
    public void unregisterOne()
    {
        final String TEST_STRING = "registrant";
        final Registry<String> sr = new Registry<String>();
        sr.register(TEST_STRING);
        sr.unregister(TEST_STRING);
        assertEmptyIteration(sr);
    }

}
