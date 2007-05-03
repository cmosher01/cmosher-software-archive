package com.surveysampling.hash.algorithm;

/*
 * Created on Mar 14, 2006
 */

public class RSHash implements HashingAlgorithm
{
    public int hash(final byte[] dataToHash)
    {
        int hash = 0;
        int a = 63689;

        for (final byte b : dataToHash)
        {
            hash = (hash * a) + b;
            a *= 378551;
        }

        return hash;
    }
}
