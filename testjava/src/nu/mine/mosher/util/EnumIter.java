package com.surveysampling.util;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumIter extends Object implements Iterator
{
    private final Enumeration e;

    public EnumIter(Enumeration e)
    {
        this.e = e;
    }

    public boolean hasNext()
    {
        return e.hasMoreElements();
    }

    public Object next() throws NoSuchElementException
    {
        return e.nextElement();
    }

    public void remove() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }
}
