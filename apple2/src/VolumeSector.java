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
     */
    public VolumeSector(DiskPos pos, int index)
    {
        this.pos = (DiskPos)pos.clone();
        this.i = index;
    }

    public String toString()
    {
        return pos.toStringTS();
    }
}
