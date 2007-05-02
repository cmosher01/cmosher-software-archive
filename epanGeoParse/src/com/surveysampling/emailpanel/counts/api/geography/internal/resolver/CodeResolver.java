/*
 * Created on May 19, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography.internal.resolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A <code>Resolver</code> that matches at most one object,
 * comparing a filtered version of the objects' toString result.
 * The filter up-cases, converts all non letters (A-Z) to spaces,
 * converts any continuous white-space to a single space, then
 * trims any leading and trailing spaces.
 * 
 * @author Chris Mosher
 */
public class CodeResolver implements Resolver
{
    private final Map mapCodeToObject = new HashMap(); // <String,Object>

    /**
     * Filters the given <code>Object</code>'s <code>toString</code> result
     * (up-cases, remove punctuation and extra white-space). The resulting string
     * must be unique across all <code>Object</code>s passed to this method
     * for this <code>Resolver</code>, otherwise an <code>IllegalStateException</code>
     * is thrown.
     * @see com.surveysampling.emailpanel.counts.api.geography.internal.resolver.Resolver#put(java.lang.Object)
     */
    public void put(final Object object) throws IllegalStateException
    {
        final String sObject = object.toString();

        if (this.mapCodeToObject.containsKey(sObject))
        {
            throw new IllegalStateException();
        }
        this.mapCodeToObject.put(sObject,object);
    }

    /**
     * Finds the matching </code>Object</code>, if there is one.
     * Appends it to <code>appendTo</code>.
     * @param code
     * @param appendTo
     * @param maxMatches
     * @return if anything was appended to <code>appendTo</code>
     */
    public boolean resolve(final String code, final Collection appendTo, final int maxMatches)
    {
        if (maxMatches <= 0)
        {
            return false;
        }

        if (!this.mapCodeToObject.containsKey(code))
        {
            return false;
        }

        return appendTo.add(this.mapCodeToObject.get(code));
    }
}
