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
     * @param newPos
     */
    public void seek(DiskPos newPos)
    {
        this.pos = newPos;
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

    public void readASCII(int len, StringBuffer sb, byte[] attrib)
    {
        if (attrib.length != len)
        {
            throw new RuntimeException("attribute array is wrong length.");
        }
        for (int i = 0; i < len; ++i)
        {
            int c = read();
            sb.append((char)(byte)(c & 0x0000007F));
        }
    }

    /**
     * @return
     */
    public int read()
    {
        int r = disk[pos.getIndex()];
        pos = pos.advance(1);
        return r;
    }

    /**
     * @return
     * @throws InvalidPosException
     */
    public DiskPos readTS() throws InvalidPosException
    {
        int track = read();
        int sector = read();
        return new DiskPos(track,sector,0,false);
    }
}
