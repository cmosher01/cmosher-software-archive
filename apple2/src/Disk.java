/*
 * Created on Sep 16, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class Disk
{
    private final byte[] disk;
    private DiskPos pos;

    /**
     * @throws InvalidPosException
     */
    public Disk() throws InvalidPosException
    {
        this.pos = new DiskPos(0,0,0,false);
    }

    public void seek(DiskPos pos)
    {
        this.pos = pos;
    }
}
