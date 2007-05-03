package com.surveysampling.hash.bloom;
import java.util.HashSet;
import java.util.Set;

import com.surveysampling.hash.algorithm.HashingAlgorithm;

/*
 * Created on Mar 14, 2006
 */

public class BloomFilter
{
    private final byte[] rBit;
    private final int cBit;
    private final Set<HashingAlgorithm> hashes;

    public BloomFilter(final int sizeInBytes, final Set<HashingAlgorithm> hashes)
    {
        this.rBit = new byte[sizeInBytes];
        this.cBit = sizeInBytes*Byte.SIZE;
        this.hashes = new HashSet<HashingAlgorithm>(20,1);
        this.hashes.addAll(hashes);
    }

    public void add(final byte[] dataToHash)
    {
        for (final HashingAlgorithm hash : this.hashes)
        {
            final int h = hash.hash(dataToHash);
            addHash(h);
        }
    }

    public boolean contains(final byte[] dataToHash)
    {
        for (final HashingAlgorithm hash : this.hashes)
        {
            final int h = hash.hash(dataToHash);
            if (!containsHash(h))
            {
               return false;
            }
        }
        return true; // could be a false positive
    }



    private void addHash(final int h)
    {
        final int iBit = positiveMod(h,this.cBit);

        this.rBit[hashIndex(iBit)] |= mask(iBit);
    }

    private boolean containsHash(final int h)
    {
        final int iBit = positiveMod(h,this.cBit);

        return (this.rBit[hashIndex(iBit)] & mask(iBit)) != 0;
    }



    private static byte mask(final int iBit)
    {
        return (byte)(1 << (iBit%Byte.SIZE));
    }

    private static int hashIndex(final int iBit)
    {
        return iBit/Byte.SIZE;
    }

    private int positiveMod(final int x, final int mod)
    {
        int r = x % mod;
        if (r < 0)
        {
            r += mod;
        }
        return r;
    }
}
