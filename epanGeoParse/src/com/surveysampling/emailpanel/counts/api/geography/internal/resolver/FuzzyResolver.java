/*
 * Created on May 16, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography.internal.resolver;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.surveysampling.util.text.FuzzyMatcher;

/**
 * A <code>Resolver</code> that fuzzy-matches a given name.
 * 
 * @author Chris Mosher
 */
public class FuzzyResolver implements Resolver
{
    private final Map mapFullToObject = new HashMap(5000); // <String,Object>
    private final Map mapWordToSet = new HashMap(5000); // <String,Set<Object>>



    /**
     * Adds an object to this resolver.
     * @param object the object to add
     */
    public void put(final Object object)
    {
        addFull(filterNameComplete(object.toString()),object);

        final String nameFiltered = filterNameLeaveDelims(object.toString());
        for (final Iterator iWord = getNamePieces(nameFiltered); iWord.hasNext();)
        {
            final String word = (String)iWord.next();
            addWord(word,object);
        }
    }

    private void addFull(final String name, final Object object)
    {
        if (this.mapFullToObject.containsKey(name))
        {
            throw new IllegalStateException();
        }
        this.mapFullToObject.put(name,object);
    }

    private void addWord(final String name, final Object object)
    {
        Set set;
        if (this.mapWordToSet.containsKey(name))
        {
            set = (Set)this.mapWordToSet.get(name);
        }
        else
        {
            set = new HashSet();
            this.mapWordToSet.put(name,set);
        }
        set.add(object);
    }

    /**
     * @param name
     * @return filtered name
     */
    private static String filterNameLeaveDelims(final String name)
    {
        String s = name;

        s = s.toUpperCase();
        s = s.replaceAll("[^-()&,/A-Z]"," ");
        s = s.replaceAll(" +"," ");
        s = s.trim();

        return s;
    }

    /**
     * @param name
     * @return filtered name
     */
    private static String filterNameComplete(final String name)
    {
        String s = name;

        s = s.toUpperCase();
        s = s.replaceAll("[^A-Z]"," ");
        s = s.replaceAll(" +"," ");
        s = s.trim();

        return s;
    }

    private static Iterator getNamePieces(final String name)
    {
        return new Iter(name);
    }

    /**
     * An <code>Iterator</code> that iterates over
     * "pieces" of a given string, delimited by
     * dashes, commas, and slashes.
     */
    private static class Iter implements Iterator
    {
        private final StringTokenizer st;
        private String next;

        Iter(final String name)
        {
            this.st = new StringTokenizer(name,"()&-,/");
            getNext();
        }

        private void getNext()
        {
            this.next = nextOrNull();
            // we only want words longer than 2 letters
            while (this.next != null && this.next.length() <= 2)
            {
                this.next = nextOrNull();
            }
        }

        private String nextOrNull()
        {
            if (!this.st.hasMoreTokens())
            {
                return null;
            }
            return this.st.nextToken().trim();
        }

        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext()
        {
            return this.next != null;
        }

        /**
         * @see java.util.Iterator#next()
         */
        public Object next() throws NoSuchElementException
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            final String s = this.next;
            getNext();
            return s;
        }

        /**
         * @see java.util.Iterator#remove()
         */
        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }
        
    }





    /**
     * Performs a "fuzzy" match on the given name.
     * 
     * @param name the name to search for
     * @param appendTo a <code>Collection</code> to append
     * resulting matches to
     * @param maxMatches maximum count of matches to return
     * @return if anything was appended to <code>appendTo</code>
     */
    public boolean resolve(final String name, final Collection appendTo, final int maxMatches)
    {
        if (maxMatches <= 0)
        {
            return false;
        }

        final String nameFiltered = filterNameComplete(name);

        // if there's an exact match, just return it
        if (this.mapFullToObject.containsKey(nameFiltered))
        {
            return appendTo.add(this.mapFullToObject.get(nameFiltered));
        }

        // otherwise, match against individual components
        final Map mapKeyToSimilarity = new HashMap(); // <DatalessKey,Similarity>

        int maxDifference = 1;
        resolveWord(nameFiltered,mapKeyToSimilarity,maxDifference);

        // try harder if necessary
        while (mapKeyToSimilarity.size() < maxMatches && maxDifference <= 7 && maxMatches > 1)
        {
            maxDifference += 3;
            resolveWord(nameFiltered,mapKeyToSimilarity,maxDifference);
        }

        final SortedSet setMatches = new TreeSet(new Comparator()
        {
            /**
             * Compares two <code>Map.Entry</code> objects
             * based on their values (which must implement
             * <code>Comparable</code>. Consistent with <code>equals</code>.
             */
            public int compare(final Object o1, final Object o2)
            {
                final Map.Entry e1 = (Map.Entry)o1;
                final Map.Entry e2 = (Map.Entry)o2;
                final Comparable cmp1 = (Comparable)e1.getValue();
                final Comparable cmp2 = (Comparable)e2.getValue();
                return cmp1.compareTo(cmp2);
            }
        });
        setMatches.addAll(mapKeyToSimilarity.entrySet());

        // find the top maxMatches matches
        int matches = 0;
        boolean changed = false;
        for (final Iterator iMatch = setMatches.iterator(); iMatch.hasNext();)
        {
            final Map.Entry entry = (Map.Entry)iMatch.next();
            changed |= appendTo.add(entry.getKey());
            if (++matches >= maxMatches)
            {
                return changed;
            }
        }
        return changed;
    }

    private void resolveWord(final String name, final Map mapKeyToSimilarity, final int difMax)
    {
        for (final Iterator iFull = this.mapFullToObject.keySet().iterator(); iFull.hasNext();)
        {
            final String full = (String)iFull.next();
            final int dif = FuzzyMatcher.difference(name,full);
            if (dif <= difMax)
            {
                addKey(this.mapFullToObject.get(full),name.length(),full.length(),dif,mapKeyToSimilarity);
            }
        }
        for (final Iterator iWord = this.mapWordToSet.keySet().iterator(); iWord.hasNext();)
        {
            final String word = (String)iWord.next();
            final int dif = FuzzyMatcher.difference(name,word);
            if (dif <= difMax)
            {
                final Set setKey = (Set)this.mapWordToSet.get(word);
                for (final Iterator iKey = setKey.iterator(); iKey.hasNext();)
                {
                    final Object object = iKey.next();
                    addKey(object,name.length(),word.length(),dif,mapKeyToSimilarity);
                }
            }
        }
    }

    private static void addKey(final Object object, final int len1, final int len2, final int dif, final Map mapKeyToSimilarity)
    {
        Similarity sim;
        if (mapKeyToSimilarity.containsKey(object))
        {
            final Similarity simExisting = (Similarity)mapKeyToSimilarity.get(object);
            sim = new Similarity(len1,len2,dif,simExisting);
        }
        else
        {
            sim = new Similarity(len1,len2,dif);
        }
        mapKeyToSimilarity.put(object,sim);
    }





    private static class Similarity implements Comparable
    {
        // use these just so we can make compareTo be
        // consistent with equals.
        private static int seq = 0;
        private final int uniqifier = ++seq;

        private final int sim;

        /**
         * @param len1
         * @param len2
         * @param lenDif
         */
        public Similarity(final int len1, final int len2, final int lenDif)
        {
            this.sim = calcSim(len1,len2,lenDif);
        }

        /**
         * @param len1
         * @param len2
         * @param lenDif
         * @param otherSimilarity
         */
        public Similarity(final int len1, final int len2, final int lenDif, final Similarity otherSimilarity)
        {
            this.sim = Math.max(calcSim(len1,len2,lenDif),otherSimilarity.sim);
        }

        private static int calcSim(final int len1, final int len2, final int lenDif)
        {
            return Math.min(len1,len2)-lenDif;
        }

        /**
         * @return the similarity value (higher numbers mean more similar)
         */
        public int getSimilarity()
        {
            return this.sim;
        }

        /**
         * Sorts by most-similar to least-similar.
         * Consistent with <code>equals</code> (<code>Object.equals</code> that is).
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(final Object object)
        {
            final Similarity that = (Similarity)object;
            /*
             * If this one has a greater similarity than
             * that one, then this one comes before that one.
             */
            if (this.sim > that.sim)
            {
                return -1;
            }
            if (that.sim > this.sim)
            {
                return +1;
            }
            if (this.uniqifier < that.uniqifier)
            {
                return -1;
            }
            if (that.uniqifier < this.uniqifier)
            {
                return +1;
            }
            return 0;
        }
    }
}
