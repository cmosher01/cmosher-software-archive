package nu.mine.mosher.random;

import java.util.Random;

/**
 * This is a wrapper for Java's java.util.Random class.
 */
public class JavaRNG extends RNGDefault implements RandomNumberGenerator
{
    private final Random rand;

    /**
     * Initializes this <code>JavaRNG</code> with the default
     * seed value.
     */
    public JavaRNG()
    {
    	this(getDefaultSeed());
    }

    /**
     * Initializes this <code>JavaRNG</code> with the given
     * seed value.
     * @param seed the seed value
     */
    public JavaRNG(final long seed)
    {
    	super(seed);
        this.rand = new Random(seed);
    }

    /**
     * Calls <code>Random.nextInt</code>.
     * @return random number
     */
    public int nextInt()
    {
        return rand.nextInt();
    }
}
