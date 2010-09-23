/**
 * 
 */
package com.ipc.uda.service.util.jmx;

import static org.junit.Assert.*;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Test;

/**
 * @author mosherc
 *
 */
public class ObjectNameFactoryTest
{
    /**
     * Test method for {@link com.ipc.uda.service.util.jmx.ObjectNameFactory#create(java.lang.String, java.lang.String, java.lang.String)}.
     * @throws MalformedObjectNameException 
     */
    @Test
    public void createNominal() throws MalformedObjectNameException
    {
        final ObjectName name = ObjectNameFactory.create("my_domain","my_name","my_type");
        assertEquals("name=my_name,type=my_type",name.getCanonicalKeyPropertyListString());
    }

    @Test(expected=MalformedObjectNameException.class)
    public void createMalformed() throws MalformedObjectNameException
    {
        ObjectNameFactory.create("malformed\ndomain\nname","my_name","my_type");
    }

    @Test
    public void getInstance() throws MalformedObjectNameException
    {
        final ObjectName name = ObjectNameFactory.create("my_domain","my_name","my_type");
        final ObjectName name2 = ObjectName.getInstance(name);
        assertTrue(name2.equals(name));
    }
}
