package com.surveysampling.mosher.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class Finder
{
    public static Object findObject(Object test, Collection r)
    {
        Iterator i = r.iterator();
        Object x = null;
        while (i.hasNext() && x == null)
        {
            Object o = i.next();
            if (test.equals(o))
                x = o;
        }
        return x;
    }

    public static Object find(Finder f, Collection r)
    {
        Iterator i = r.iterator();
        Object x = null;
        while (i.hasNext() && x == null)
        {
            Object o = i.next();
            if (f.is(o))
                x = o;
        }
        return x;
    }

    public static void findAll(Finder f, Collection r, List ls)
    {
        Iterator i = r.iterator();
        while (i.hasNext())
        {
            Object o = i.next();
            if (f.is(o))
                ls.add(o);
        }
    }

    abstract protected boolean is(Object x);
}
