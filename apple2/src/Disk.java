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
     * @param disk
     * @throws InvalidPosException
     */
    public Disk(byte[] disk) throws InvalidPosException
    {
        this.disk = disk;
        this.pos = new DiskPos(0,0,0,false);
    }

    /**
     * @param pos
     */
    public void seek(DiskPos pos)
    {
        this.pos = pos;
    }

    /**
     * @param len
     * @return
     */
    public byte[] read(int len)
    {
        byte[] rb = new byte[len];
        System.arraycopy(disk,pos.getIndex(),rb,0,len);
        pos = pos.advance(len);
        return rb;
    }

    public int read()
    {
        int r = disk[pos.getIndex()];
        pos = pos.advance(1);
        return r;
    }

    public DiskPos readTS() throws InvalidPosException
    {
        int track = read();
        int sector = read();
        return new DiskPos(track,sector,0,false);
    }
}
