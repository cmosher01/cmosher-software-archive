/*
 * Created on Sep 18, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class TSMap
{
    private final boolean[] r = new boolean[DiskPos.cSectorsPerTrack*DiskPos.cTracksPerDisk];

    /**
     * @param p
     */
    public void mark(DiskPos p)
    {
        set(p,true);
    }

    /**
     * @param p
     */
    public void clear(DiskPos p)
    {
        set(p,false);
    }

    /**
     * @param p
     * @param mark
     */
    public void set(DiskPos p, boolean mark)
    {
        int sector = p.getSectorInDisk();
        if (sector < 0 || r.length <= sector)
        {
            throw new IllegalArgumentException();
        }
        r[sector] = mark;
    }

    /**
     * @param p
     * @return
     */
    public boolean isMarked(DiskPos p)
    {
        int sector = p.getSectorInDisk();
        if (sector < 0 || r.length <= sector)
        {
            throw new IllegalArgumentException();
        }
        return r[sector];
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (int i = 0; i < r.length; i++)
        {
            boolean b = r[i];
            if (b)
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    sb.append(",");
                }
                sb.append(Integer.toHexString(i));
            }
        }
        return sb.toString();
    }

    public static TSMap intersection(TSMap a, TSMap b)
    {
        TSMap m = new TSMap();
        for (int i = 0; i < a.r.length; i++)
        {
            m.r[i] = a.r[i] && b.r[i];
        }
        return m;
    }
}
