package com.surveysampling.mosher.test;

import java.util.Collection;
import java.util.Iterator;

public abstract class Applyer
{
    public static void apply(Applyer a, Collection r)
    {
        Iterator i = r.iterator();
        while (i.hasNext())
        {
            Object x = i.next();
            a.execute(x);
        }
    }

    abstract protected void execute(Object x);
}
