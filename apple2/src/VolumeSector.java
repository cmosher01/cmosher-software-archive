/*
 * Created on Oct 9, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeSector
{
    private final DiskPos pos;
    private final int i;

    /**
     * @param pos
     * @param index
     */
    public VolumeSector(DiskPos pos, int index)
    {
        this.pos = (DiskPos)pos.clone();
        this.i = index;
    }

    /**
     * @return
     */
    public String toString()
    {
        return pos.toStringTS();
    }

    /**
     * @return
     */
    public DiskPos getPos()
    {
        return (DiskPos)pos.clone();
    }

    public int getIndex()
    {
        return i;
    }
}
