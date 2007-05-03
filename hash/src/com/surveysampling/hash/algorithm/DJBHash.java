package com.surveysampling.hash.algorithm;

/*
 * Created on Mar 14, 2006
 */

public class DJBHash implements HashingAlgorithm
{
    public int hash(final byte[] dataToHash)
    {
        int hash = 5381;

        for (final byte b : dataToHash)
        {
            hash += (hash << 5) + b;
        }

        return hash;
    }
}
