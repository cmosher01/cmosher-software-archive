package edu.fsu.stat.geo.random;

import nu.mine.mosher.random.RandomNumberGenerator;

public class Mwc implements RandomNumberGenerator
{
    private static long a = 611373678L;
    private final long seed;
    private int[] Q = new int[1038];
    private int i = 0;
    private int c = 362436;

    public Mwc()
    {
        this(System.currentTimeMillis());
    }

    public Mwc(long seed)
    {
        this.seed = seed;
        init();
    }

    protected void init()
    {
        RandomNumberGenerator rand = new Kiss(seed);
        for (int i = 0; i < Q.length; ++i)
            Q[i] = rand.nextInt();
    }

    public long getSeed()
    {
        return seed;
    }

    public int nextInt()
    {
        if (i == 0)
            i = Q.length-1;

        long t = a * Q[i--] + c;

        c = (int)(t >> 32);
        Q[i] = (int)t;

        return Q[i];
    }
}
