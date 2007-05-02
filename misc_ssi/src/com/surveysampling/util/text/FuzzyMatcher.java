/*
 * Created on May 17, 2005
 */
package com.surveysampling.util.text;

/**
 * Provides for fuzzy matching of strings.
 * Pass two strings to <code>difference</code>, and
 * the result is an indication of how different, in
 * terms of spelling, the two strings are. Zero means
 * they are the same; higher numbers mean they are
 * more different.
 * 
 * @author Chris Mosher
 */
public final class FuzzyMatcher
{
    private FuzzyMatcher()
    {
        assert false;
    }

    /**
     * Compares two strings to see how different they are.
     * Runs in <code>O(s1.length*s2.length)</code> time,
     * using at least <code>sizeof(int)*s1.length*s2.length</code> of heap.
     * @param s1 string (to search for)
     * @param s2 potential match
     * @return difference between <code>s1</code> and <code>s2</code>; higher
     * numbers mean the strings are more different; zero means the strings are the same
     */
    public static int difference(final String s1, final String s2)
    {
        // get string lengths
        final int c1 = s1.length();
        final int c2 = s2.length();

        /*
         * Initialize the array of differences,
         * for example, for s1 len 3 and s2 len 4:
         *   0 1 2 3 4
         *   1 0 0 0 0
         *   2 0 0 0 0
         *   3 0 0 0 0
         */
        final int[][] d = new int[c1+1][c2+1];
        for (int i1 = 0; i1 <= c1; ++i1)
        {
            d[i1][0] = i1;
        }
        for (int i2 = 0; i2 <= c2; ++i2)
        {
            d[0][i2] = i2;
        }

        /*
         * Match each character from the first string
         * with each character from the second string.
         */
        for (int i1 = 0; i1 < c1; ++i1)
        {
            final char a1 = s1.charAt(i1);
            for (int i2 = 0; i2 < c2; ++i2)
            {
                final char a2 = s2.charAt(i2);

                /*
                 * Accumulate counts of inserts, deletions, and
                 * changes into the array of differences.
                 */
                d[i1+1][i2+1] = Math.min(Math.min(
                    d[i1][i2+1]+1,
                    d[i1+1][i2]+1),
                    d[i1][i2] + (a1==a2 ? 0 : 1));
            }
        }

        return d[c1][c2];
    }
}
