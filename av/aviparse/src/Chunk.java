import java.io.IOException;

/*
 * Created on Aug 10, 2004
 */


/**
 * @author Chris Mosher
 */
public class Chunk
{
    /**
     * Four character code defining this chunk.
     */
    protected final FourCC fourCC;
    /**
     * Size of this chunk, in bytes.
     */
    protected final long siz;
    /**
     * Size of this chunk, including padding, in bytes.
     */
    protected final long sizPadded;

    /**
     * @param fourCC
     * @param siz
     */
    protected Chunk(FourCC fourCC, long siz)
    {
        this.fourCC = fourCC;
        this.siz = siz;
        if (this.siz < 0)
        {
            throw new IllegalArgumentException("size must be positive");
        }

        if ((siz & 1) == 1)
        {
            ++siz;
        }
        this.sizPadded = siz;
    }

    /**
     * @param fourCC
     * @param siz
     * @return
     */
    public static Chunk createChunk(FourCC fourCC, long siz)
    {
        Chunk chunk;
        if (fourCC.isList())
        {
            chunk = new ListChunk(fourCC,siz);
        }
        else
        {
            chunk = new Chunk(fourCC,siz);
        }
        return chunk;
    }

    /**
     * @param in
     * @param remain
     * @throws IOException
     * @throws IOException
     */
    public void readFrom(RiffInputStream in, Remainder remain) throws IOException
    {
        remain.verifyHave(sizPadded);

        Remainder remainChunk = new Remainder(sizPadded);

        while (remainChunk.have())
        {
            long skipped = in.skip(remainChunk.remainder);
            remainChunk.take(skipped);
        }

        remain.take(sizPadded);
    }

    /**
     * @return
     */
    public String toString()
    {
        return stats();
    }

    /**
     * @return
     */
    public String stats()
    {
        return fourCC+" ("+siz+" bytes)";
    }
}
