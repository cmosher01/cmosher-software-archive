package nu.mine.mosher.random;

public interface RandomNumberGenerator
{
    //Ctor() { this(System.currentTimeMillis()); }
    //Ctor(long seed);
    long getSeed();
    int nextInt();
}
