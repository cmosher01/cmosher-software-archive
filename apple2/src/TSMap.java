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
        int sector = p.getSectorInDisk();
        if (sector < 0 || r.length <= sector)
        {
            throw new IllegalArgumentException();
        }
        r[sector] = true;
    }

    /**
     * @param p
     */
    public void clear(DiskPos p)
    {
        int sector = p.getSectorInDisk();
        if (sector < 0 || r.length <= sector)
        {
            throw new IllegalArgumentException();
        }
        r[sector] = false;
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
}
