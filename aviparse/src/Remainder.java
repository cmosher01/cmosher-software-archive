import java.io.IOException;

/*
 * Created on Aug 10, 2004
 */


/**
 * TODO
 * 
 * @author chrism
 */
public class Remainder
{
    /**
     * Comment for <code>remainder</code>
     */
    public long remainder;

    /**
     * @param r
     */
    public Remainder(long r)
    {
        remainder = r;
    }

    /**
     * @param i
     */
    public void take(long i)
    {
        remainder -= i;
    }

    /**
     * @param i
     * @throws IOException
     */
    public void verifyHave(long i) throws IOException
    {
        if (!have(i))
        {
            throw new IOException("Not enough bytes remaining");
        }
    }

    /**
     * @param i
     * @return
     */
    public boolean have(long i)
    {
        return (i <= remainder);
    }

    /**
     * @return
     */
    public boolean have()
    {
        return (remainder > 0);
    }
}
