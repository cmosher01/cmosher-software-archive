import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Created on Aug 10, 2004
 */


/**
 * @author Chris Mosher
 */
public class ListChunk extends Chunk
{
    /**
     * Comment for <code>type</code>
     */
    protected FourCC type;
    /**
     * Comment for <code>chunks</code>
     */
    protected List chunks = new ArrayList();

    /**
     * @param fourCC
     * @param siz
     */
    public ListChunk(FourCC fourCC, long siz)
    {
        super(fourCC,siz);
    }

    /**
     * @param in
     * @param remain
     * @throws IOException
     */
    public void readFrom(RiffInputStream in, Remainder remain) throws IOException
    {
        remain.verifyHave(sizPadded);

        Remainder remainList = new Remainder(sizPadded);

        type = in.readFourCC();
        remainList.take(4);

        while (remainList.have())
        {
            Chunk chunk = in.readChunk(remainList);
            chunks.add(chunk);
        }

        remain.take(sizPadded);
    }

    /**
     * @return
     */
    public String toString()
    {
        return stats()+" \""+type+"\"";
    }

    /**
     * Pretty print.
     */
    public void pp()
    {
        ppPrivate(0);
    }

    private void ppPrivate(int lev)
    {
        indent(lev);
        System.out.println(this);
        for (Iterator i = this.chunks.iterator(); i.hasNext();)
        {
            Chunk chunk = (Chunk)i.next();
            if (chunk instanceof ListChunk)
            {
                ((ListChunk)chunk).ppPrivate(lev+1);
            }
            else
            {
                indent(lev+1);
                System.out.println(chunk);
            }
        }
    }

    /**
     * @param lev
     */
    private static void indent(int lev)
    {
        for (int i = 0; i < lev; ++i)
        {
            System.out.print("    ");
        }
    }
}
