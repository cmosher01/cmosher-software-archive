package nu.mine.mosher.random;

import java.util.Random;

/**
 * This is a wrapper for Java's java.util.Random class.
 */
public class JavaRNG implements RandomNumberGenerator
{
    private final long seed;
    private final Random rand;

    /**
     * Constructor for JavaRNG.
     */
    public JavaRNG()
    {
        this(System.currentTimeMillis());
    }

    public JavaRNG(long seed)
    {
        this.seed = seed;
        this.rand = new Random(seed);
    }

    /**
     * @see nu.mine.mosher.random.RandomNumberGenerator#getSeed()
     */
    public long getSeed()
    {
        return seed;
    }

    /**
     * @see nu.mine.mosher.random.RandomNumberGenerator#nextInt()
     */
    public int nextInt()
    {
        return rand.nextInt();
    }
}
