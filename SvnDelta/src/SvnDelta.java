import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * Created on Sep 7, 2004
 */


/**
 * Translation of Subversion's VDelta algorithm
 * into Java.
 * 
 * @author Chris Mosher
 */
public class SvnDelta
{
    private final int cWindow = 4;
    private final Map map = new HashMap();

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        FileInputStream streamSrc = new FileInputStream(new File(args[0]));
        FileInputStream streamTrg = new FileInputStream(new File(args[1]));
        FileChannel channelSrc = streamSrc.getChannel();
        FileChannel channelTrg = streamTrg.getChannel();
        long cSrc = streamSrc.available();
        long cTrg = streamTrg.available();
        ByteBuffer data = ByteBuffer.allocate((int)(cSrc+cTrg)); // TODO
        channelSrc.read(data);
        channelTrg.read(data);
        SvnDelta delta = new SvnDelta();
        delta.vdelta(data,cSrc,cTrg);
    }

    /**
     * @param data
     * @param cSrc
     * @param cTrg
     */
    public void vdelta(ByteBuffer data, long cSrc, long cTrg)
    {
        data.position(0);
        data.limit((int)cSrc); // TODO
        vdeltaPrivate(data,false);

        data.position((int)cSrc);
        data.limit((int)(cSrc+cTrg)); // TODO
        vdeltaPrivate(data,true);
    }
    private void vdeltaPrivate(ByteBuffer data, boolean output)
    {
        long start = data.position();
        long end = data.limit();
        long here = start;
        long insertFrom = -1;
        while (true)
        {
            if (end-here < cWindow)
            {
                long from;
                if (insertFrom < 0)
                {
                    from = here;
                }
                else
                {
                    from = insertFrom;
                }
                if (output && from < end)
                {
                    byte[] rb = new byte[(int)(end-from)];
                    data.position((int)from); // TODO long
                    data.get(rb);
                    doData(rb);
                }
                return;
            }

            long matchCurrent = -1;
            long cMatchCurrent = 0;
            long key = here;
            boolean progress;
            do
            {
                progress = false;
                Collection vals = (Collection)map.get(getBucket(data,key));
                if (vals != null)
                {
                    for (Iterator i = vals.iterator(); i.hasNext();)
                    {
                        long there = ((Long)i.next()).longValue();
                        if (there < key-here)
                        {
                            continue;
                        }
                        long match = there-(key-here);
                        long cMatch = extendMatch(data,match,here);
                        if (match < start && match+cMatch > start)
                        {
                            cMatch = start-match;
                        }
                        if (cMatch >= cWindow && cMatch > cMatchCurrent)
                        {
                            matchCurrent = match;
                            cMatchCurrent = cMatch;
                            progress = true;
                        }
                    }
                    if (progress)
                    {
                        key = here+cMatchCurrent-cWindow+1;
                    }
                }
            }
            while (progress & end-key >= cWindow);

            if (cMatchCurrent < cWindow)
            {
                if (!output)
                {
                    addMapping(data,here);
                }
                if (insertFrom < 0)
                {
                    insertFrom = here;
                }
                ++here;
                continue;
            }
            else if (output)
            {
                if (insertFrom >= 0)
                {
                    byte[] rb = new byte[(int)(here-insertFrom)];
                    data.position((int)insertFrom); // TODO long
                    data.get(rb);
                    doData(rb);
                    insertFrom = -1;
                }
                doCopy(matchCurrent,cMatchCurrent,(start < matchCurrent));
            }
            here += cMatchCurrent;
            if (end-here >= cWindow && !output)
            {
                for (long last = here-cWindow+1; last < here; ++last)
                {
                    addMapping(data,last);
                }
            }
        }
    }

    /**
     * @param data
     * @param where
     */
    private void addMapping(ByteBuffer data, long where)
    {
        Object key = getBucket(data,where);
        Collection vals = (Collection)map.get(key);
        if (vals == null)
        {
            vals = new ArrayList();
            map.put(key, vals);
        }
        vals.add(new Long(where));
    }

    /**
     * @param rb
     * @return
     */
    private Object hash(byte[] rb)
    {
        int hash = 0;
        for (int i = 0; i < rb.length; i++)
        {
            hash *= 127;
            hash += rb[i];
        }
        return new Integer(hash); // TODO mod?
    }

    /**
     * @param data
     * @param match
     * @param from
     * @return
     */
    private long extendMatch(ByteBuffer data, long match, long from)
    {
        long here = from;
        while (here < data.limit() && data.get((int)match) == data.get((int)here))
        {
            ++match;
            ++here;
        }
        return here - from;
    }

    /**
     * @param data
     * @param key
     * @return
     */
    private Object getBucket(ByteBuffer data, long key)
    {
        data.position((int)key); // TODO
        byte[] rb = new byte[cWindow];
        data.get(rb);
        return hash(rb);
    }

    /**
     * @param data
     */
    private void doData(byte[] data)
    {
        System.out.println("DATA ["+data.length+" bytes]");
    }

    /**
     * @param offset
     * @param length
     * @param fromTarget
     */
    private void doCopy(long offset, long length, boolean fromTarget)
    {
        System.out.println("COPY "+(fromTarget?"trg":"src")+"@"+offset+", "+length);
    }
}
