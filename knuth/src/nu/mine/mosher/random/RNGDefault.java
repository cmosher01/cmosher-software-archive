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
	 * @return
	 */
	public long getSeed()
	{
		return this.seed;
	}

	/**
	 * @return
	 */
	abstract public int nextInt();
}
