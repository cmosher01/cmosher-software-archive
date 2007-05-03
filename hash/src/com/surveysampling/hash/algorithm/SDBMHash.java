package com.surveysampling.hash.algorithm;

/*
 * Created on Mar 14, 2006
 */

public class SDBMHash implements HashingAlgorithm
{
    public int hash(final byte[] dataToHash)
    {
        int hash = 0;

        for (final byte b : dataToHash)
        {
            hash = (hash << 16) + (hash << 6) - hash + b;
        }

        return hash;
    }
}
