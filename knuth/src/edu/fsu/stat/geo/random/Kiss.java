package edu.fsu.stat.geo.random;

import nu.mine.mosher.random.RNGDefault;
import nu.mine.mosher.random.RandomNumberGenerator;

public class Kiss extends RNGDefault implements RandomNumberGenerator
{
    private static final long a = 698769069L;
    private final long seed;
    private int x;
    private int y = 362436;
    private int z;
    private int c = 7654321;

    public Kiss()
    {
        this(getDefaultSeed());
    }

    public Kiss(long seed)
    {
        this.seed = seed;
        this.x = (int)seed;
        this.z = (int)(seed >> 32);
    }

    public long getSeed()
    {
        return seed;
    }

    public int nextInt()
    {
        x *= 69069;
        x += 12345;
        y ^= (y << 13);
        y ^= (y >> 17);
        y ^= (y << 5);

        long t = a * z + c;
        c = (int)(t >> 32);
        z = (int)t;

        return x + y + z;
    }
}
