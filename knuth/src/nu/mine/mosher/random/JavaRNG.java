package nu.mine.mosher.random;

import java.util.Random;

/**
 * This is a wrapper for Java's java.util.Random class.
 */
public class JavaRNG extends RNGDefault implements RandomNumberGenerator
{
    private final Random rand;

    /**
     * Constructor for JavaRNG.
     */
    public JavaRNG()
    {
    	super();
    }

    public JavaRNG(long seed)
    {
        this.rand = new Random(seed);
    }

    /**
     * Calls <code>Random.nextInt</code>.
     */
    public int nextInt()
    {
        return rand.nextInt();
    }
}
