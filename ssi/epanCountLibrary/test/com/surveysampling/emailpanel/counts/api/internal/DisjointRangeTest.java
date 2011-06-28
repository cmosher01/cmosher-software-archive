/*
 * Created on Jun 9, 2005
 */
package com.surveysampling.emailpanel.counts.api.internal;

import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

/**
 * Test <code>DisjointRange</code>.
 * 
 * @author Chris Mosher
 */
public class DisjointRangeTest extends TestCase
{
    /**
     * test conjoinSet on empty set
     */
    public void testConjoinSetEmpty()
    {
        final SortedSet set = new TreeSet();
        assertTrue(set.isEmpty());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertTrue(setConj.isEmpty());
        assertTrue(set.isEmpty());
    }

    /**
     * test conjoinSet on set of one
     */
    public void testConjoinSetOne()
    {
        final SortedSet set = new TreeSet();
        set.add(new DisjointRange(5,10));
        assertEquals(1,set.size());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertEquals(1,setConj.size());

        assertTrue(setConj.contains(new DisjointRange(5,10)));
    }

    /**
     * test conjoinSet on set of two disjoint ranges
     */
    public void testConjoinSetTwoDisjoint()
    {
        final SortedSet set = new TreeSet();
        set.add(new DisjointRange(5,10));
        set.add(new DisjointRange(30,70));
        assertEquals(2,set.size());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertEquals(2,setConj.size());

        assertTrue(setConj.contains(new DisjointRange(5,10)));
        assertTrue(setConj.contains(new DisjointRange(30,70)));
    }

    /**
     * test conjoinSet on set of 3 disjoint ranges
     */
    public void testConjoinSetThreeDisjoint()
    {
        final SortedSet set = new TreeSet();
        set.add(new DisjointRange(5,10));
        set.add(new DisjointRange(20,25));
        set.add(new DisjointRange(30,70));
        assertEquals(3,set.size());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertEquals(3,setConj.size());

        assertTrue(setConj.contains(new DisjointRange(5,10)));
        assertTrue(setConj.contains(new DisjointRange(20,25)));
        assertTrue(setConj.contains(new DisjointRange(30,70)));
    }

    /**
     * test conjoinSet on set of two conjoint ranges
     */
    public void testConjoinSetTwoConjoint()
    {
        final SortedSet set = new TreeSet();
        set.add(new DisjointRange(5,10));
        set.add(new DisjointRange(10,16));
        assertEquals(2,set.size());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertEquals(1,setConj.size());

        assertTrue(setConj.contains(new DisjointRange(5,16)));
    }

    /**
     * test conjoinSet on set of two conjoint and
     * one disjoint ranges
     */
    public void testConjoinSetTwoConjointOneDisjoint1()
    {
        final SortedSet set = new TreeSet();
        set.add(new DisjointRange(5,8));
        set.add(new DisjointRange(8,15));
        set.add(new DisjointRange(16,20));
        assertEquals(3,set.size());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertEquals(2,setConj.size());

        assertTrue(setConj.contains(new DisjointRange(5,15)));
        assertTrue(setConj.contains(new DisjointRange(16,20)));
    }

    /**
     * test conjoinSet on set of two conjoint and
     * one disjoint ranges
     */
    public void testConjoinSetTwoConjointOneDisjoint2()
    {
        final SortedSet set = new TreeSet();
        set.add(new DisjointRange(5,8));
        set.add(new DisjointRange(10,16));
        set.add(new DisjointRange(16,20));
        assertEquals(3,set.size());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertEquals(2,setConj.size());

        assertTrue(setConj.contains(new DisjointRange(5,8)));
        assertTrue(setConj.contains(new DisjointRange(10,20)));
    }

    /**
     * test conjoinSet on set of two conjoint and
     * two disjoint ranges
     */
    public void testConjoinSetTwoConjointTwoDisjoint1()
    {
        final SortedSet set = new TreeSet();
        set.add(new DisjointRange(5,8));
        set.add(new DisjointRange(8,16));
        set.add(new DisjointRange(20,30));
        set.add(new DisjointRange(40,50));
        assertEquals(4,set.size());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertEquals(3,setConj.size());

        assertTrue(setConj.contains(new DisjointRange(5,16)));
        assertTrue(setConj.contains(new DisjointRange(20,30)));
        assertTrue(setConj.contains(new DisjointRange(40,50)));
    }

    /**
     * test conjoinSet on set of two conjoint and
     * two disjoint ranges
     */
    public void testConjoinSetTwoConjointTwoDisjoint2()
    {
        final SortedSet set = new TreeSet();
        set.add(new DisjointRange(5,8));
        set.add(new DisjointRange(15,20));
        set.add(new DisjointRange(20,30));
        set.add(new DisjointRange(40,50));
        assertEquals(4,set.size());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertEquals(3,setConj.size());

        assertTrue(setConj.contains(new DisjointRange(5,8)));
        assertTrue(setConj.contains(new DisjointRange(15,30)));
        assertTrue(setConj.contains(new DisjointRange(40,50)));
    }

    /**
     * test conjoinSet on set of two conjoint and
     * two disjoint ranges
     */
    public void testConjoinSetTwoConjointTwoDisjoint3()
    {
        final SortedSet set = new TreeSet();
        set.add(new DisjointRange(5,8));
        set.add(new DisjointRange(15,20));
        set.add(new DisjointRange(30,40));
        set.add(new DisjointRange(40,50));
        assertEquals(4,set.size());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertEquals(3,setConj.size());

        assertTrue(setConj.contains(new DisjointRange(5,8)));
        assertTrue(setConj.contains(new DisjointRange(15,20)));
        assertTrue(setConj.contains(new DisjointRange(30,50)));
    }

    /**
     * test conjoinSet on set of three conjoint ranges
     */
    public void testConjoinSetThreeConjoint()
    {
        final SortedSet set = new TreeSet();
        set.add(new DisjointRange(5,10));
        set.add(new DisjointRange(10,16));
        set.add(new DisjointRange(16,20));
        assertEquals(3,set.size());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertEquals(1,setConj.size());

        assertTrue(setConj.contains(new DisjointRange(5,20)));
    }


    /**
     * test conjoinSet on set of four conjoint ranges
     */
    public void testConjoinSetFourConjoint()
    {
        final SortedSet set = new TreeSet();
        set.add(new DisjointRange(5,10));
        set.add(new DisjointRange(10,16));
        set.add(new DisjointRange(16,20));
        set.add(new DisjointRange(20,50));
        assertEquals(4,set.size());

        final SortedSet setConj = DisjointRange.conjoinSet(set);
        assertEquals(1,setConj.size());

        assertTrue(setConj.contains(new DisjointRange(5,50)));
    }
}
