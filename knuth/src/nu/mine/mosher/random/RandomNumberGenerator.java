package nu.mine.mosher.random;

/**
 * A generator of a random sequence of numbers. Implementers of this interface
 * should provide a constructor that takes an integer used to seed the sequence.
 * Users then call <code>getSeed</code> (repeatedly if desired) to get a random
 * number (or sequence of numbers).
 *
 * @author Chris Mosher
 */
public interface RandomNumberGenerator
{
    /**
     * Gets the seed, as passed in to the constructor.
     * @return the original seed
     */
    long getSeed();

    /**
     * Gets the next number in this generator's sequence of
     * random numbers.
     * @return random number
     */
    int nextInt();
}
