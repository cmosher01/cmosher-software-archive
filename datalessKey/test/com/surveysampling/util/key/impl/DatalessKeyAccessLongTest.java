/*
 * Created on Jun 6, 2005
 */
package com.surveysampling.util.key.impl;

import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;

import junit.framework.TestCase;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class DatalessKeyAccessLongTest extends TestCase
{
    private DatalessKeyAccess access;
    protected void setUp() throws Exception
    {
        super.setUp();
        this.access = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");
    }

    /**
     * 
     */
    public void testCreateKeyFromString()
    {
        final DatalessKey key1 = this.access.createKeyFromString("1");
        final String s1 = this.access.keyAsString(key1);
        final DatalessKey key2 = this.access.createKeyFromString(s1);
        final String s2 = this.access.keyAsString(key2);
        assertEquals(s1,s2);
    }

    /**
     * 
     */
    public void testEquals()
    {
        final DatalessKey key1 = this.access.createKeyFromString("1");
        final DatalessKey key2 = this.access.createKeyFromString("0x1");
        assertEquals(key1,key2);
        assertEquals(key2,key1);
    }

    /**
     * 
     */
    public void testNullEquals()
    {
        final DatalessKey key1 = this.access.createNullKey();
        final DatalessKey key2 = this.access.createNullKey();
        assertEquals(key1,key2);
        assertEquals(key2,key1);
    }

    /**
     * 
     */
    public void testNotEquals()
    {
        final DatalessKey key1 = this.access.createKeyFromString("1");
        final DatalessKey key2 = this.access.createKeyFromString("2");
        assertFalse(key1.equals(key2));
        assertFalse(key2.equals(key1));
    }

    /**
     * 
     */
    public void testNullNotEquals()
    {
        final DatalessKey key1 = this.access.createNullKey();
        final DatalessKey key2 = this.access.createKeyFromString("1");
        assertFalse(key1.equals(key2));
        assertFalse(key2.equals(key1));
    }

    /**
     * 
     */
    public void testNegative()
    {
        final DatalessKey key1 = this.access.createKeyFromString("-1");
        final DatalessKey key2 = this.access.createNullKey();
        assertFalse(key1.equals(key2));
        assertFalse(key2.equals(key1));
    }

    /**
     * 
     */
    public void testAccess()
    {
        final DatalessKey key = this.access.createKeyFromString("1");
        System.out.println(key.toString());
        assertTrue(DatalessKeyAccessFactory.getDatalessKeyAccess(key) instanceof DatalessKeyAccessLong);
    }
}
