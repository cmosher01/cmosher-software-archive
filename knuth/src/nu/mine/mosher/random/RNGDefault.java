/*
 * Created on February 16, 2005
 */
package nu.mine.mosher.random;

/**
 * Helper for classes implementing <code>RandomNumberGenerator</code>.
 *
 * @author Chris Mosher
 */
public abstract class RNGDefault implements RandomNumberGenerator
{
	private final long seed;

	/**
	 * Initializes the <code>RandomNumberGenerator</code> using
	 * <code>System.currentTimeMillis()</code> as a seed number.
	 */
	public RNGDefault()
	{
        this(System.currentTimeMillis());
	}

	/**
	 * Initializes the <code>RandomNumberGenerator</code> with
	 * the given seed number.
	 * @param seed the random generator's seed
	 */
	public RNGDefault(final long seed)
	{
		this.seed = seed;
	}

    /**
     * Gets the seed (as passed in to the constructor).
     * @return the original seed
     */
	public long getSeed()
	{
		return this.seed;
	}

    /**
     * Gets the next number in this generator's sequence of
     * random numbers. The number will typically be randomized
     * across all 32 bits of the returned <code>int</code>.
     * @return random number
     */
	abstract public int nextInt();
}
