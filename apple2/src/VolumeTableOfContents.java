/*
 * Created on Oct 9, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeTableOfContents extends VolumeEntity
{
    /**
     * @param pos
     */
    public VolumeTableOfContents(final DiskPos pos)
    {
        rSector.add(pos);
    }
}
