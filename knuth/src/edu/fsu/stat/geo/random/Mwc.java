package edu.fsu.stat.geo.random;

import nu.mine.mosher.random.RNGDefault;
import nu.mine.mosher.random.RandomNumberGenerator;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class Mwc extends RNGDefault implements RandomNumberGenerator
{
    private static long a = 611373678L;

    private int[] Q = new int[1038];
    private int i = 0;
    private int c = 362436;

    /**
     * 
     */
    public Mwc()
    {
        this(getDefaultSeed());
    }

    /**
     * @param seed
     */
    public Mwc(final long seed)
    {
        super(seed);
        init();
    }

    protected void init()
    {
        RandomNumberGenerator rand = new Kiss(getSeed());
        for (int x = 0; i < Q.length; ++x)
        {
            Q[x] = rand.nextInt();
        }
    }

    /**
     * @return random number
     */
    public int nextInt()
    {
        if (i <= 0)
        {
            i = Q.length-1;
        }

        long t = a * Q[i--] + c;

        c = (int)(t >> 32);
        Q[i] = (int)t;

        return Q[i];
    }
}
