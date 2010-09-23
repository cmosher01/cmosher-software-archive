package com.ipc.uda.types;



import static org.junit.Assert.*;

import org.junit.Test;



/**
 * @author mosherc
 * 
 */
public class UIDTest
{

    /**
     * Test method for {@link com.ipc.uda.types.UID#equals(java.lang.Object)}.
     */
    @Test
    public void nominal()
    {
        final String TESTUID = "123456789abcdefg";
        final UID uid = new UID(TESTUID);
        assertEquals(TESTUID,uid.toString());
    }

    /**
     * Test method for {@link com.ipc.uda.types.UID#toString()}.
     */
    @Test
    public void equalsNominal()
    {
        final String TESTUID = "123456789abcdefg";
        final UID uid1 = new UID(TESTUID);
        final UID uid2 = new UID(TESTUID);
        assertTrue(uid1.equals(uid2));
        assertTrue(uid2.equals(uid1));
    }


    /**
     * Test method for {@link com.ipc.uda.types.UID#toString()}.
     */
    @Test
    public void hashCodeNominal()
    {
        final String TESTUID = "123456789abcdefg";
        final UID uid1 = new UID(TESTUID);
        final UID uid2 = new UID(TESTUID);
        assertTrue(uid1.hashCode() == uid2.hashCode());
    }

    @Test(expected=RuntimeException.class)
    public void uidNull()
    {
        new UID(null);
    }

    @Test(expected=RuntimeException.class)
    public void uidEmpty()
    {
        new UID("");
    }

    @Test
    public void equalsNonUid()
    {
        final String TESTUID = "123456789abcdefg";
        final UID uid1 = new UID(TESTUID);
        assertFalse(uid1.equals(new Object()));
    }

    @Test
    public void asDsIdNominal()
    {
        final String TESTUID = "12345";
        final UID uid1 = new UID(TESTUID);
        assertEquals(12345,uid1.asDataServicesID());
    }

    @Test(expected=RuntimeException.class)
    public void asDsIdInvalid()
    {
        final String TESTUID = "abc";
        final UID uid1 = new UID(TESTUID);
        uid1.asDataServicesID();
    }
}
