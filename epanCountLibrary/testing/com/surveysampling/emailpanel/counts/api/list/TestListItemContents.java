/*
 * Created on April 20, 2005
 */
package com.surveysampling.emailpanel.counts.api.list;

/**
 * An immutable structure representing just some
 * arbitrary set of data. In this case, we just
 * have a name.
 * 
 * @author Chris Mosher
 */
public class TestListItemContents
{
    private final String name;

    /**
     * @param name
     */
    public TestListItemContents(final String name)
    {
        this.name = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return this.name;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof TestListItemContents))
        {
            return false;
        }
        final TestListItemContents that = (TestListItemContents) obj;
        return this.name.equals(that.name);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return this.name.hashCode();
    }
}
