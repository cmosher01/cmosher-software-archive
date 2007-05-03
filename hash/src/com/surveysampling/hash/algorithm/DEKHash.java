package com.surveysampling.hash.algorithm;

/*
 * Created on Mar 14, 2006
 */

public class DEKHash implements HashingAlgorithm
{
    public int hash(final byte[] dataToHash)
    {
        int hash = dataToHash.length;

        for (final byte b : dataToHash)
        {
            hash = (hash << 5) ^ (hash >> 27) ^ b;
        }

        return hash;
    }
}
