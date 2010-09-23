package com.ipc.uda.service.util;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class OptionalTest
{

    @Test
    public void exists()
    {
        final String TEST = "testing";
        final Optional<String> os = new Optional<String>(TEST);
        assertTrue(os.exists());
    }

    @Test
    public void notExists()
    {
        final String TEST = null;
        final Optional<String> os = new Optional<String>(TEST);
        assertFalse(os.exists());
    }

    private static class Foo
    {
        private final int x;
        public Foo(int x) { this.x = x; }
        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Foo))
                return false;
            return this.x == ((Foo)obj).x;
        }
        @Override
        public int hashCode() { return this.x; }
    }

    @Test
    public void equals()
    {
        final Foo TEST1 = new Foo(74);
        final Optional<Foo> o1 = new Optional<Foo>(TEST1);

        final Foo TEST2 = new Foo(74);
        final Optional<Foo> o2 = new Optional<Foo>(TEST2);

        assertNotSame(TEST1,TEST2);
        assertNotSame(o1,o2);

        assertEquals(TEST1,TEST2);
        assertEquals(o1,o2);
    }

    @Test
    public void getExists()
    {
        final Foo TEST = new Foo(99);
        final Optional<Foo> opt = new Optional<Foo>(TEST);
        final Foo got = opt.get();
        assertSame(got,TEST);
    }

    @Test(expected=Optional.FieldDoesNotExist.class)
    public void getNotExists()
    {
        final Foo TEST = null;
        final Optional<Foo> opt = new Optional<Foo>(TEST);
        opt.get();
    }

    @Test
    public void notEqualsNull()
    {
        final Foo TEST1 = new Foo(38);
        final Optional<Foo> o1 = new Optional<Foo>(TEST1,true);

        assertTrue(!o1.equals(null));
    }

    @Test
    public void equalsNulls()
    {
        final Foo TEST1 = null;
        final Optional<Foo> o1 = new Optional<Foo>(TEST1,true);

        final Foo TEST2 = null;
        final Optional<Foo> o2 = new Optional<Foo>(TEST2);

        assertTrue(o1.equals(o2));
    }

    @Test
    public void notEqualsNulls()
    {
        final Foo TEST1 = null;
        final Optional<Foo> o1 = new Optional<Foo>(TEST1,false);

        final Foo TEST2 = null;
        final Optional<Foo> o2 = new Optional<Foo>(TEST2);

        assertFalse(o1.equals(o2));
    }

    @Test
    public void toStringNominal()
    {
        final String TEST = "testing";
        final Optional<String> o = new Optional<String>(TEST);
        final String got = o.toString();
        assertEquals(TEST,got);
    }

    @Test
    public void toStringNull()
    {
        final Optional<String> o = new Optional<String>(null);
        final String got = o.toString();
        assertEquals("[null]",got);
    }

    @Test
    public void nothingObject()
    {
        final Nothing<String> os = new Nothing<String>();
        assertFalse(os.exists());
    }

    @Test
    public void nominalSet()
    {
        final Foo TEST1 = new Foo(74);
        final Optional<Foo> o1 = new Optional<Foo>(TEST1);

        final Set<Optional<Foo>> set = new HashSet<Optional<Foo>>();
        set.add(o1);
        assertEquals(1,set.size());

        final Foo TEST2 = new Foo(74);
        final Optional<Foo> o2 = new Optional<Foo>(TEST2);
        set.add(o2);
        assertEquals(1,set.size());
    }

    @Test
    public void notExistsSet()
    {
        final Foo TEST1 = null;
        final Optional<Foo> o1 = new Optional<Foo>(TEST1);

        final Set<Optional<Foo>> set = new HashSet<Optional<Foo>>();
        set.add(o1);
        assertEquals(1,set.size());

        final Foo TEST2 = null;
        final Optional<Foo> o2 = new Optional<Foo>(TEST2);
        set.add(o2);
        assertEquals(1,set.size());
    }
}
