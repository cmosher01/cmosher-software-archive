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
	private final int seed;

	/**
	 * @param seed
	 */
	public RNGDefault(final int seed)
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
