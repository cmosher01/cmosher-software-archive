package edu.stanford.cs.knuth.sa.random;

import nu.mine.mosher.random.RNGDefault;
import nu.mine.mosher.random.RandomNumberGenerator;

/**
 * 32-bit version of Knuth's latest (as of 2003).
 * 
 * @author Chris Mosher
 */
public class RanArray extends RNGDefault implements RandomNumberGenerator
{
    private static final int KK = 100;
    private static final int LL = 37;
    private static final long MM = 1L<<32;

    private long[] ranx = new long[KK];

    private long[] ranbuf;
    private int nextat = KK;



    /**
     * 
     */
    public RanArray()
    {
        this(getDefaultSeed());
    }

    /**
     * @param seed
     */
    public RanArray(final long seed)
    {
        super(seed);
        init();
    }

    /**
     * @return
     */
    public synchronized int nextInt()
    {
        if (nextat >= KK)
        {
            ranbuf = generate(1009);
            nextat = 0;
        }
        return (int)ranbuf[nextat++];
    }

    protected void init()
    {
        long[] x = new long[KK+KK-1];

        long ss = (getSeed()+2)&(MM-2);
        for (int j = 0; j < KK; ++j)
        {
            x[j] = ss;
            ss <<= 1;
            if (ss >= MM)
            {
                ss -= MM-2;
            }
        }
        ++x[1];


        ss = modDiff(getSeed(),0);
        int t = 69;
        while (t > 0)
        {
            for (int j = KK-1; j > 0; --j)
            {
                x[j+j] = x[j];
                x[j+j-1] = 0;
            }
            for (int j = KK+KK-2; j >= KK; --j)
            {
                x[j-(KK-LL)] = modDiff(x[j-(KK-LL)],x[j]);
                x[j-KK] = modDiff(x[j-KK],x[j]);
            }
    
            if (ss%2 == 1)
            {
                for (int j = KK-1; j >= 0; --j)
                {
                    x[j+1] = x[j];
                }
                x[0] = x[KK];
                x[LL] = modDiff(x[LL],x[KK]);
            }
            if (ss != 0)
            {
                ss >>= 1;
            }
            else
            {
                --t;
            }
        }

        for (int j = 0; j < LL; ++j)
        {
            ranx[j+KK-LL] = x[j];
        }
        for (int j = LL; j < KK; ++j)
        {
            ranx[j-LL] = x[j];
        }
        for (int j = 0; j < 10; ++j)
        {
            generate(KK+KK-1);
        }
    }

    protected synchronized long[] generate(final int n)
    {
        if (n < KK)
        {
            throw new IllegalArgumentException("n must be >= "+KK);
        }

        long[] x = new long[n];

        for (int j = 0; j < KK; ++j)
        {
            x[j] = ranx[j];
        }

        for (int j = KK; j < n; ++j)
        {
            x[j] = modDiff(x[j-KK],x[j-LL]);
        }

        for (int j = 0; j < LL; ++j)
        {
            ranx[j] = modDiff(x[n+j-KK],x[n+j-LL]);
        }

        for (int j = LL; j < KK; ++j)
        {
            ranx[j] = modDiff(x[n+j-KK],ranx[j-LL]);
        }

        return x;
    }

    private static long modDiff(final long x, final long y)
    {
        return (x-y)&(MM-1);
    }
}
