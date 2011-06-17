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
    private final VolumeEntity parent;

    /**
     * @param pos
     * @param index
     */
    public VolumeSector(DiskPos pos, int index, VolumeEntity parent)
    {
        this.pos = (DiskPos)pos.clone();
        this.i = index;
        this.parent = parent;
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

    /**
     * @return
     */
    public int getIndex()
    {
        return i;
    }
}
