import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public static String convertASCII(byte[] rb)
    {
        return convertASCII(rb,0,rb.length);
    }

    public static String convertASCII(byte[] rb, int pos, int len)
    {
        StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; ++i)
        {
            int c = rb[pos+i];
            sb.append((char)(byte)(c & 0x0000007F));
        }
        return sb.toString();
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

    /**
     * @return
     */
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
        rewind();
        while (!EOF())
        {
            DiskPos cur = this.pos;
            byte[] sector = read(DiskPos.cSector);
            if ((sector[3]==3 || sector[3]==2) &&
                match(sector,0x04,new byte[]{0x00,0x00}) &&
                match(sector,0x34,new byte[]{0x23,0x10,0x00,0x01}) &&
                match(sector,0x3a,new byte[]{0x00,0x00}))
            {
                System.out.println("VTOC @ T$"+Integer.toHexString(cur.getTrackInDisk())+", S$"+Integer.toHexString(cur.getSectorInTrack()));
            }
        }
    }

    /**
     * @throws InvalidPosException
     */
    public void findDos33CatalogSector() throws InvalidPosException
    {
        rewind();
        while (!EOF())
        {
            DiskPos cur = this.pos;
            byte[] sector = read(DiskPos.cSector);
            if (sector[0] == 0 &&
//                sector[1] > 0 &&
                DiskPos.isValidSector(sector[2]) &&
                sector[3] == 0)
            {
                // check catalog entries
                int ce = 0x0B;
                int penultimateSpace = 0;
                int goodEntries = 0;
                boolean live = true;
                List entries = new ArrayList();
                boolean valid = true;
                for (int cat = 0; cat < 7 && live && valid; ++cat)
                {
                    if (sector[ce] == 0)
                    {
                        live = false;
                    }
                    else
                    {
                        ++goodEntries;
                    }
                    if (live && DiskPos.isValidSector(sector[ce+1]) &&
                        isValidFileType(sector[ce+2]))
                    {
//                        if (isValidFileName(sector,ce+3))
//                        {
                            if (sector[ce] == -1)
                            {
                                entries.add(convertASCII(sector,ce+3,29).trim()+" [deleted]");
                            }
                            else
                            {
                                entries.add(convertASCII(sector,ce+3,30)+" [T/S map @ T$"+Integer.toHexString(sector[ce])+", S$"+Integer.toHexString(sector[ce+1])+"]");
                            }
                            if (sector[ce+31] == (byte)0xA0)
                            {
                                ++penultimateSpace;
                            }
//                        }
                    }
                    else if (live)
                    {
                        valid = false;
                    }
                    ce += 35;
                }
                if (valid && ((goodEntries==1 && penultimateSpace==1) || (goodEntries > 1 && penultimateSpace >= goodEntries-2)) && goodEntries > 0)
                {
                    System.out.println("Catalog Sector @ T$"+Integer.toHexString(cur.getTrackInDisk())+", S$"+Integer.toHexString(cur.getSectorInTrack())+" ("+goodEntries+" entries)");
                    for (Iterator i = entries.iterator(); i.hasNext();)
                    {
                        String f = (String)i.next();
                        System.out.print("    \"");
                        System.out.print(f.trim());
                        System.out.println("\"");
                    }
                }
            }
        }
    }

    /**
     * @throws InvalidPosException
     */
    public void findDos33TSMapSector() throws InvalidPosException
    {
        rewind();
        while (!EOF())
        {
            DiskPos cur = this.pos;
            byte[] sector = read(DiskPos.cSector);
            if (sector[0]==0 &&
                    DiskPos.isValidTrack(sector[1]) && DiskPos.isValidSector(sector[2]) &&
                    sector[3]==0 && sector[4]==0 &&
                    word(sector,5)%0x7A == 0 &&
                    match(sector,7,new byte[]{0,0,0,0,0}) &&
                    (sector[0x0C] > 0 || sector[0x0D] > 0) &&
                    DiskPos.isValidTrack(sector[0x0C]) && DiskPos.isValidSector(sector[0x0D]))
                    {
                        int ts = 0x0E;
                        boolean valid = true;
                        while (ts+1 < sector.length && (sector[ts] != 0 || sector[ts+1] != 0) && valid)
                        {
                            if (!(DiskPos.isValidTrack(sector[ts]) && DiskPos.isValidSector(sector[ts+1])))
                            {
                                valid = false;
                            }
                            ts += 2;
                        }
                        while (ts < sector.length && valid)
                        {
                            if (sector[ts++] != 0)
                            {
                                valid = false;
                            }
                        }
                        if (valid)
                        {
                            System.out.println("T/S map @ T$"+Integer.toHexString(cur.getTrackInDisk())+", S$"+Integer.toHexString(cur.getSectorInTrack()));
                        }
                    }
        }
    }

    /**
     * @param sector
     * @param i
     * @return
     */
    private int word(byte[] sector, int i)
    {
        int lo = sector[i++];
        int hi = sector[i];
        return (lo | (hi << 8)) & 0xFF;
    }

    /**
     * @param sector
     * @param i
     * @return
     */
    private boolean isValidFileName(byte[] sector, int i)
    {
        // only check 29 chars of filename (last char could be overwritten by file deletion)
        for (int x = 0; x < 29; ++x)
        {
            if ((sector[i+x] & 0x80) == 0)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * @param b
     * @return
     */
    private boolean isValidFileType(byte b)
    {
        b &= 0x7F;
        return b==0 || b==0x01 || b==0x02 || b==0x04 || b==0x08 ||
            b==0x10 || b==0x20 || b==0x40;
    }

    /**
     * @param actual
     * @param pos
     * @param expected
     * @return
     */
    private boolean match(byte[] actual, int pos, byte[] expected)
    {
        for (int i = 0; i < expected.length; ++i)
        {
            if (actual[pos+i] != expected[i])
            {
                return false;
            }
        }
        return true;
    }
}
