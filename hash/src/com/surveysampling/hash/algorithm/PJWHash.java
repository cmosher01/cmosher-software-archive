package com.surveysampling.hash.algorithm;

/*
 * Created on Mar 14, 2006
 */

public class PJWHash implements HashingAlgorithm
{
    public int hash(final byte[] dataToHash)
    {
        long hash = 0;

        for (final byte b : dataToHash)
        {
            hash = (hash << 4) + b;

            final long nib = hash >> Integer.SIZE;

            if (nib != 0)
            {
                hash = (int)(hash + nib);
            }
        }

        return (int)hash;
    }
}
