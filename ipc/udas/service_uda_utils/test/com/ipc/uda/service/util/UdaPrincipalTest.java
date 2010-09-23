package com.ipc.uda.service.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.util.HashSet;

public class UdaPrincipalTest
{
    @Test
    public void nominal()
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "test";
            }
        };
        final UdaPrincipal udaPrincipal = new UdaPrincipal(p);
        assertEquals("test",udaPrincipal.getName());
        assertEquals("test",udaPrincipal.toString());
    }

    @Test
    public void equal()
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "test";
            }
        };
        final UdaPrincipal uda1 = new UdaPrincipal(p);
        final UdaPrincipal uda2 = new UdaPrincipal(p);

        assertNotSame(uda1,uda2);
        assertTrue(uda1.equals(uda2));
        assertTrue(uda2.equals(uda1));
        assertTrue(uda1.hashCode() == uda2.hashCode());
    }

    @Test
    public void differentNamesNotEqual()
    {
        final Principal p1 = new Principal()
        {
            @Override
            public String getName()
            {
                return "test1";
            }
        };
        final Principal p2 = new Principal()
        {
            @Override
            public String getName()
            {
                return "test2";
            }
        };
        final UdaPrincipal uda1 = new UdaPrincipal(p1);
        final UdaPrincipal uda2 = new UdaPrincipal(p2);

        assertFalse(uda1.equals(uda2));
        assertFalse(uda2.equals(uda1));
    }

    @Test
    public void differentClassNotEqual()
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "test";
            }
        };
        final UdaPrincipal uda = new UdaPrincipal(p);

        assertFalse(uda.equals("test"));
    }

    @Test
    public void collection()
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "test";
            }
        };

        final HashSet<UdaPrincipal> set = new HashSet<UdaPrincipal>();

        final UdaPrincipal uda1 = new UdaPrincipal(p);
        set.add(uda1);

        final UdaPrincipal uda2 = new UdaPrincipal(p);
        assertTrue(set.contains(uda2));
    }

    @Test
    public void serialization() throws IOException, ClassNotFoundException
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "test";
            }
        };


        final UdaPrincipal original = new UdaPrincipal(p);

        // serialize
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(original);
        oos.flush();
        oos.close();

        //deserialize
        final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        final Object obj = ois.readObject();
        final UdaPrincipal copy = (UdaPrincipal)obj;

        assertEquals(original,copy);
        assertEquals("test",copy.getName());
    }

    @Test(expected=IllegalStateException.class)
    public void nullPrincipal()
    {
        new UdaPrincipal((Principal)null);
    }

    @Test(expected=IllegalStateException.class)
    public void nullPrincipalName()
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return null;
            }
        };
        new UdaPrincipal(p);
    }
}
