
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Chris Mosher
 */
public class RiffInputStream extends FilterInputStream
{
    /**
     * @param stream
     */
    public RiffInputStream(InputStream stream)
    {
        super(stream);
    }

    /**
     * @return
     * @throws IOException
     */
    public FourCC readFourCC() throws IOException
    {
        byte[] fourcc = new byte[4];
        int c = in.read(fourcc);
        if (c == -1)
        {
            throw new EOFException();
        }
        if (c != 4)
        {
            throw new IOException("Error reading FourCC");
        }
        return new FourCC(new String(fourcc,"ISO-8859-1"));
    }

    /**
     * @return
     * @throws IOException
     */
    public int readSize() throws IOException
    {
        int i = 0;

        int bt = 0;
        for (int b = 0; b < 4; ++b)
        {
            int x = in.read();
            if (x == -1)
            {
                throw new EOFException();
            }
            x <<= bt;
            i |= x;
            bt += 8;
        }

        return i;
    }

    /**
     * @param remain
     * @return
     * @throws IOException
     */
    public Chunk readChunk(Remainder remain) throws IOException
    {
        remain.verifyHave(8);
        Chunk chunk = Chunk.createChunk(readFourCC(), readSize());
        remain.take(8);

        chunk.readFrom(this,remain);

        return chunk;
    }
}
