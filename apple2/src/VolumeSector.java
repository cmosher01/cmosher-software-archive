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
        this.pos = pos;
        this.i = index;
    }
}
