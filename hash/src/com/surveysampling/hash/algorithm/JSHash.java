package com.surveysampling.hash.algorithm;

/*
 * Created on Mar 14, 2006
 */

public class JSHash implements HashingAlgorithm
{
    public int hash(final byte[] dataToHash)
    {
        int hash = 1315423911;

        for (final byte b : dataToHash)
        {
            hash ^= (hash << 5) + (hash >> 2) + b;
        }

        return hash;
    }
}
