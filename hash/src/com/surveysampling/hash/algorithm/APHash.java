package com.surveysampling.hash.algorithm;

/*
 * Created on Mar 14, 2006
 */

public class APHash implements HashingAlgorithm
{
    public int hash(final byte[] dataToHash)
    {
        int hash = 0;

        boolean tog = false;
        for (final byte b : dataToHash)
        {
            tog = !tog;
            if (tog)
            {
                hash ^= (hash << 7) ^ (hash >> 3) ^ b;
            }
            else
            {
                hash ^= ~((hash << 11) ^ (hash >> 5) ^ b);
            }
        }

        return hash;
    }
}
