package edu.fsu.stat.geo.diehard.tests;

import java.util.logging.Logger;

import nu.mine.mosher.random.RandomNumberGenerator;

public abstract class RandomnessTest
{
    protected Logger mLog = Logger.global;
    protected final RandomNumberGenerator rng;

    abstract public double test() throws Exception;

    public RandomnessTest(RandomNumberGenerator rng)
    {
        this.rng = rng;
    }

    protected long next()
    {
        int intr = rng.nextInt();

        long r = (long)intr;

        if (intr < 0)
            r += 1L<<32;

        return r;
    }
}
