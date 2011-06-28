/*
 * Created on May 20, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography.internal.resolver;

import java.util.Collection;

/**
 * Resolves a given name against a set of objects.
 * Objects that implement this interface allow a caller
 * to put objects into this <code>Resolver</code> and
 * later resolve a given name to a set of objects.
 * Examples include a fuzzy resolver, where a given
 * name is fuzzy-matched against the objects' toString
 * results.
 * 
 * @author Chris Mosher
 */
public interface Resolver
{
    /**
     * Adds the given <code>Object</code> to the set
     * of objects that this <code>Resolver</code> will
     * match against. If a given object <code>equals</code>
     * an object already in this <code>Resolver</code>, then
     * the new object replaces the existing object.
     * @param object the object to add
     */
    void put(Object object);

    /**
     * Resolves the given name to a (possibly empty)
     * set of objects, as defined by the specific
     * implementation (such as, a fuzzy match of the given
     * name against the objects' <code>toString</code> values).
     * Appends the resulting set to <code>appendTo</code>.
     * 
     * @param name the name to search for
     * @param appendTo a <code>Collection</code> to append
     * resulting matches to
     * @param maxMatches maximum count of matches to return
     * (for a fuzzy search, for example)
     * @return if anything was appended to <code>appendTo</code>
     */
    boolean resolve(String name, Collection appendTo, int maxMatches);
}
