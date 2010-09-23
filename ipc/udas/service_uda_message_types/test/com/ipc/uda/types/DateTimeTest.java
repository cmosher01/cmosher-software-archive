/**
 * 
 */
package com.ipc.uda.types;

import static junit.framework.Assert.*;

import org.junit.Test;


/**
 * @author mosherc
 *
 */
public class DateTimeTest
{
    /**
     * Creates a DateTime from a properly formatted date-time string,
     * and makes sure the result is not the epoch start time (zero).
     */
    @Test
    public void creationNominal()
    {
        final String TEST_TIME = "2010-01-13T12:42:13";
        final DateTime t = new DateTime(TEST_TIME);
        assertTrue(t.asDate().getTime() != 0L);
    }

    /**
     * Creates a DateTime from a properly formatted date-time string,
     * and makes sure the toString method returns the same string.
     */
    @Test
    public void toStringNominal()
    {
        final String TEST_TIME = "2010-01-13T12:42:13";
        final DateTime t = new DateTime(TEST_TIME);
        assertEquals(TEST_TIME,t.toString());
    }

    /**
     * Creates a DateTime from an invalid string,
     * and makes sure the result is equal to the epoch start time (zero).
     */
    @Test
    public void creationFromBadFormat()
    {
        final String TEST_BAD_TIME = "2010-01-13_BAD_12:42:13";
        final DateTime t = new DateTime(TEST_BAD_TIME);
        assertEquals(0L,t.asDate().getTime());
    }

    /**
     * Creates a DateTime from an empty string,
     * and makes sure the result is equal to the epoch start time (zero).
     */
    @Test
    public void creationFromEmptyString()
    {
        final String TEST_TIME = "";
        final DateTime t = new DateTime(TEST_TIME);
        assertEquals(0L,t.asDate().getTime());
    }

    /**
     * Creates a DateTime from an null pointer,
     * and makes sure the result is equal to the epoch start time (zero).
     */
    @Test
    public void creationFromNull()
    {
        final String TEST_TIME = null;
        final DateTime t = new DateTime(TEST_TIME);
        assertEquals(0L,t.asDate().getTime());
    }

    @Test
    public void equalsNominal()
    {
        final String TEST_TIME = "2010-01-13T12:42:13";
        final DateTime t1 = new DateTime(TEST_TIME);
        final DateTime t2 = new DateTime(TEST_TIME);
        assertTrue(t1.equals(t2));
        assertTrue(t2.equals(t1));
    }

    @Test
    public void notEquals()
    {
        final String TEST_TIME1 = "2010-01-13T12:42:13";
        final String TEST_TIME2 = "2010-01-13T12:42:14";
        final DateTime t1 = new DateTime(TEST_TIME1);
        final DateTime t2 = new DateTime(TEST_TIME2);
        assertFalse(t1.equals(t2));
        assertFalse(t2.equals(t1));
    }

    @Test
    public void notEqualsWrongClass()
    {
        final String TEST_TIME = "2010-01-13T12:42:13";
        final DateTime t = new DateTime(TEST_TIME);
        assertFalse(t.equals(new Object()));
    }
}
