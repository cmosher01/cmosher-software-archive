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

    public void rewind() throws InvalidPosException
    {
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

    /**
     * @param len
     * @return
     */
    public String readASCII(int len)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; ++i)
        {
            int c = read();
            sb.append((char)(byte)(c & 0x0000007F));
        }
        return sb.toString();
    }

    /**
     * @param len
     * @param sb
     * @param attrib array of attributes (0=inverse, 1=flash, 2=normal)
     */
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
            byte a;
            if (c < 0x40)
            {
                a = 1; // inverse
            }
            else if (c < 0x80)
            {
                a = 2; // flash
            }
            else
            {
                a = 0; // normal
            }
            attrib[i] = a;
        }
    }

    /**
     * @return
     */
    public int read()
    {
        int r = disk[pos.getIndex()];
        r &= 0x000000FF;
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

    public boolean EOF()
    {
        return this.pos.getIndex() >= this.disk.length;
    }
    /**
     * @throws InvalidPosException
     * 
     */
    public void findDos33VTOC() throws InvalidPosException
    {
        int track = 0;
        int sector = 0;
        seek(new DiskPos(track,sector,0,true));
        while (!EOF())
        {
        }
    }
}
