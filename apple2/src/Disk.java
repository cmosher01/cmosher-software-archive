import java.util.Collection;
import java.util.Iterator;

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
//    private DiskPos pos;

    /**
     * @param disk
     */
    public Disk(byte[] disk)
    {
        this.disk = disk;
        try
        {
            this.pos = new DiskPos(0,0,0);
        }
        catch (InvalidPosException e)
        {
            throw new RuntimeException("shouldn't happen",e);
        }
    }

    /**
     */
    public void rewind()
    {
        try
        {
            this.pos = new DiskPos(0,0,0);
        }
        catch (InvalidPosException e)
        {
            throw new RuntimeException("shouldn't happen",e);
        }
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
     * @param rb
     * @return
     */
    public static String convertASCII(byte[] rb)
    {
        return convertASCII(rb,0,rb.length);
    }

    /**
     * @param rb
     * @param pos
     * @param len
     * @return
     */
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
        return new DiskPos(track,sector,0);
    }

    /**
     * @return
     */
    public boolean EOF()
    {
        return this.pos.getIndex() >= this.disk.length;
    }

    /**
     * @param rPosVtoc
     */
    public void findDos33VTOC(Collection rPosVtoc)
    {
        rewind();
        while (!EOF())
        {
            DiskPos cur = this.pos;
            byte[] sector = read(DiskPos.cSector);
            if (isDos33VTOC(sector))
            {
                rPosVtoc.add(cur);
//                System.out.println("VTOC @ T$"+Integer.toHexString(cur.getTrackInDisk())+", S$"+Integer.toHexString(cur.getSectorInTrack()));
            }
        }
    }

    /**
     * @param sector
     * @return
     */
    static boolean isDos33VTOC(byte[] sector)
    {
        return
            (sector[3]==3 || sector[3]==2 || sector[3]==1) &&
            sector[4]==0 &&
            (sector[5]==0 || sector[5]==4) &&
            (match(sector,0x34,new byte[]{0x23,0x10,0x00,0x01}) || match(sector,0x34,new byte[]{0x23,0x10,0x01,0x00})) &&
            match(sector,0x3a,new byte[]{0x00,0x00});
    }

    /**
     * @param rPosCat
     */
    public void findDos33CatalogSector(/*boolean allowLarge, TSMap tsmapMaps*/Collection rPosCat)
    {
        rewind();
        while (!EOF())
        {
            DiskPos cur = this.pos;
            byte[] sector = read(DiskPos.cSector);
//            List entries = new ArrayList();
            int goodEntries = isDos33CatalogSector(sector/*,allowLarge,tsmapMaps,entries*/);
            if (goodEntries > 0)
            {
                rPosCat.add(cur);
//                System.out.println("Catalog Sector @ T$"+Integer.toHexString(cur.getTrackInDisk())+", S$"+
//                        Integer.toHexString(cur.getSectorInTrack())+" ("+goodEntries+" entries)");
//                for (Iterator i = entries.iterator(); i.hasNext();)
//                {
//                    String f = (String)i.next();
//                    System.out.print("    \"");
//                    System.out.print(f.trim());
//                    System.out.println("\"");
//                }
            }
        }
    }

//    /**
//     * @param sector
//     * @param allowLarge
//     * @param tsmapMaps
//     * @param entries
//     * @return
//     */
//    static int isDos33CatalogSector(byte[] sector, boolean allowLarge, TSMap tsmapMaps, List entries)
//    {
//        if (sector[0] == 0 &&
//            DiskPos.isValidTrack(sector[1],allowLarge) &&
//            DiskPos.isValidSector(sector[2]) &&
//            sector[3] == 0)
//        {
//            // check catalog entries
//            int ce = 0x0B;
//            int penultimateSpace = 0;
//            int goodEntries = 0;
//            boolean live = true;
//            boolean valid = true;
//            for (int cat = 0; cat < 7 && live && valid; ++cat)
//            {
//                if (sector[ce] == 0)
//                {
//                    live = false;
//                }
//                else
//                {
//                    ++goodEntries;
//                }
//                if (live && 
//                    (DiskPos.isValidTrack(sector[ce],allowLarge) || sector[ce] == -1) &&
//                    DiskPos.isValidSector(sector[ce+1]) &&
//                    isValidFileType(sector[ce+2]))
//                {
//                    boolean deleted = (sector[ce] == -1);
//                    int trk;
//                    if (deleted)
//                    {
//                        trk = sector[ce+32];
//                    }
//                    else
//                    {
//                        trk = sector[ce];
//                    }
//                    StringBuffer sb = new StringBuffer(64);
//                    sb.append(convertASCII(sector,ce+3,30));
//                    int csect = word(sector,ce+33);
//                    sb.append(" ["+csect+" sector");
//                    if (csect > 1)
//                    {
//                        sb.append("s, T/S map");
//                        DiskPos tsm;
//                        try
//                        {
//                            tsm = new DiskPos(trk,sector[ce+1],0,allowLarge);
//                        }
//                        catch (InvalidPosException e)
//                        {
//                            throw new RuntimeException("shouldn't happen");
//                        }
//                        tsmapMaps.mark(tsm);
//                    }
//                    sb.append(" @ T$"+Integer.toHexString(trk)+", S$"+Integer.toHexString(sector[ce+1])+"]");
//                    if (deleted)
//                    {
//                        sb.append(" [deleted]");
//                    }
//                    entries.add(sb.toString());
//                    if (sector[ce+31] == (byte)0xA0)
//                    {
//                        ++penultimateSpace;
//                    }
//                }
//                else if (live)
//                {
//                    valid = false;
//                }
//                ce += 35;
//            }
//            if (valid && (goodEntries == penultimateSpace || penultimateSpace >= 3) && goodEntries > 0)
//            {
//                return goodEntries;
//            }
//        }
//        return 0;
//    }

    /**
     * @param sector
     * @return
     */
    static int isDos33CatalogSector(byte[] sector)
    {
        if (sector[0] == 0 &&
            DiskPos.isValidTrack(sector[1]) &&
            DiskPos.isValidSector(sector[2]) &&
            sector[3] == 0)
        {
            // check catalog entries
            int ce = 0x0B;
            int penultimateSpace = 0;
            int goodEntries = 0;
            boolean live = true;
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
                if (live && 
                    (DiskPos.isValidTrack(sector[ce]) || sector[ce] == -1) &&
                    DiskPos.isValidSector(sector[ce+1]) &&
                    isValidFileType(sector[ce+2]))
                {
                    if (sector[ce+31] == (byte)0xA0)
                    {
                        ++penultimateSpace;
                    }
                }
                else if (live)
                {
                    valid = false;
                }
                ce += 35;
            }
            if (valid && (goodEntries == penultimateSpace || penultimateSpace >= 3) && goodEntries > 0)
            {
                return goodEntries;
            }
        }
        return 0;
    }

    /**
     * @param sector
     * @param entries
     * @throws InvalidPosException
     */
    public static void getDos33CatalogEntries(byte[] sector, Collection entries) throws InvalidPosException
    {
        int p = 0x0B;
        while (p < 0x100 && sector[p] != 0)
        {
            boolean deleted = (sector[p] == -1);

            int trk;
            if (deleted)
            {
                trk = sector[p+0x20];
            }
            else
            {
                trk = sector[p];
            }

            boolean lck = (sector[p+2] & 0x80) != 0;

            int fil = (sector[p+2] & 0x7F);

            int nameLen = deleted ? 29 : 30;
            byte[] name = new byte[nameLen];
            System.arraycopy(sector, p+3, name, 0, nameLen);

            int cSector = word(sector,p+0x21);

            entries.add(new Dos33CatalogEntry(deleted,new DiskPos(trk,sector[p+1],0),lck,fil,cSector,name));

            p += 0x23;
        }
    }

    /**
     * @param sector
     * @param entries
     * @throws InvalidPosException
     */
    public static void getDos33TSMapEntries(byte[] sector, Collection entries) throws InvalidPosException
    {
        int p = 0x0C;
        while (p < 0x100 && (sector[p] != 0 || sector[p+1] != 0))
        {
            entries.add(new DiskPos(sector[p],sector[p+1],0));

            p += 2;
        }
    }

    /**
     * @param sector
     * @return
     * @throws InvalidPosException
     */
    public static DiskPos getDos33Next(byte[] sector) throws InvalidPosException
    {
        return new DiskPos(sector[1],sector[2],0);
    }

    /**
     * @param rDiskPos list of DiskPos (track/sectors)
     * @return
     */
    public byte[] getDos33File(Collection rDiskPos)
    {
        byte[] r = new byte[rDiskPos.size()*DiskPos.cSector];
        int ir = 0;
        for (Iterator i = rDiskPos.iterator(); i.hasNext();)
        {
            DiskPos p = (DiskPos)i.next();
            System.arraycopy(this.disk, p.getIndex(), r, ir, DiskPos.cSector);
            ir += DiskPos.cSector;
        }
        return r;
    }

    /**
     * @param rDiskPosSectorsWithData
     */
    public void getDataTS(Collection rDiskPosSectorsWithData)
    {
        rewind();
        while (!EOF())
        {
            DiskPos cur = this.pos;
            byte[] sector = read(DiskPos.cSector);
            if (hasData(sector))
            {
                rDiskPosSectorsWithData.add(cur);
            }
        }
    }

    /**
     * @param m
     */
    public void getDataTS(TSMap m)
    {
        rewind();
        while (!EOF())
        {
            DiskPos cur = this.pos;
            byte[] sector = read(DiskPos.cSector);
            if (hasData(sector))
            {
                m.mark(cur);
            }
        }
    }

    /**
     * @param sector
     * @return
     */
    private static boolean hasData(byte[] sector)
    {
        for (int i = 0; i < sector.length; ++i)
        {
            byte b = sector[i];
            if (b != 0)
            {
                return true;
            }
        }
        return false;
    }

//    /**
//     * @param allowLarge
//     * @param tsmapMaps
//     * @throws InvalidPosException
//     */
//    public void findDos33CatalogSectorX(boolean allowLarge, TSMap tsmapMaps) throws InvalidPosException
//    {
//        rewind();
//        while (!EOF())
//        {
//            DiskPos cur = this.pos;
//            byte[] sector = read(DiskPos.cSector);
//            if (sector[0] == 0 &&
//                DiskPos.isValidTrack(sector[1],allowLarge) &&
//                DiskPos.isValidSector(sector[2]) &&
//                sector[3] == 0)
//            {
//                // check catalog entries
//                int ce = 0x0B;
//                int penultimateSpace = 0;
//                int goodEntries = 0;
//                boolean live = true;
//                List entries = new ArrayList();
//                boolean valid = true;
//                for (int cat = 0; cat < 7 && live && valid; ++cat)
//                {
//                    if (sector[ce] == 0)
//                    {
//                        live = false;
//                    }
//                    else
//                    {
//                        ++goodEntries;
//                    }
//                    if (live && 
//                        (DiskPos.isValidTrack(sector[ce],allowLarge) || sector[ce] == -1) &&
//                        DiskPos.isValidSector(sector[ce+1]) &&
//                        isValidFileType(sector[ce+2]))
//                    {
//                        boolean deleted = (sector[ce] == -1);
//                        int trk;
//                        if (deleted)
//                        {
//                            trk = sector[ce+32];
//                        }
//                        else
//                        {
//                            trk = sector[ce];
//                        }
//                        StringBuffer sb = new StringBuffer(64);
//                        sb.append(convertASCII(sector,ce+3,30));
//                        int csect = word(sector,ce+33);
//                        sb.append(" ["+csect+" sector");
//                        if (csect > 1)
//                        {
//                            sb.append("s, T/S map");
//                            DiskPos tsm = new DiskPos(trk,sector[ce+1],0,allowLarge);
//                            tsmapMaps.mark(tsm.getSectorInDisk());
//                        }
//                        sb.append(" @ T$"+Integer.toHexString(trk)+", S$"+Integer.toHexString(sector[ce+1])+"]");
//                        if (deleted)
//                        {
//                            sb.append(" [deleted]");
//                        }
//                        entries.add(sb.toString());
//                        if (sector[ce+31] == (byte)0xA0)
//                        {
//                            ++penultimateSpace;
//                        }
//                    }
//                    else if (live)
//                    {
//                        valid = false;
//                    }
//                    ce += 35;
//                }
//                if (valid && (goodEntries == penultimateSpace || penultimateSpace >= 3) && goodEntries > 0)
//                {
//                    System.out.println("Catalog Sector @ T$"+Integer.toHexString(cur.getTrackInDisk())+", S$"+Integer.toHexString(cur.getSectorInTrack())+" ("+goodEntries+" entries)");
//                    for (Iterator i = entries.iterator(); i.hasNext();)
//                    {
//                        String f = (String)i.next();
//                        System.out.print("    \"");
//                        System.out.print(f.trim());
//                        System.out.println("\"");
//                    }
//                }
//            }
//        }
//    }

    /**
     * @param rPosTSMaps
     */
    public void findDos33TSMapSector(/*TSMap tsmapMapsInCatalog*/Collection rPosTSMaps)
    {
        rewind();
        while (!EOF())
        {
            DiskPos cur = this.pos;
            byte[] sector = read(DiskPos.cSector);
            if (isDos33TSMapSector(sector))
            {
                rPosTSMaps.add(cur);
//                System.out.print("T/S map @ T$"+Integer.toHexString(cur.getTrackInDisk())+", S$"+Integer.toHexString(cur.getSectorInTrack()));
//                if (sector[1] != 0 || sector[2] != 0)
//                {
//                    System.out.print(" (next @ T$"+Integer.toHexString(sector[1])+", S$"+Integer.toHexString(sector[2])+")");
//                }
//                if (tsmapMapsInCatalog.isMarked(cur))
//                {
//                    System.out.print(" (cataloged)");
//                }
//                else
//                {
//                    System.out.print(" (orphaned)");
//                }
//                System.out.println();
            }
        }
    }

    /**
     * @param sector
     * @return
     */
    static boolean isDos33TSMapSector(byte[] sector)
    {
        boolean valid = false;
        if (sector[0]==0 &&
            DiskPos.isValidTrack(sector[1]) && DiskPos.isValidSector(sector[2]) &&
            sector[3]==0 && sector[4]==0 &&
            word(sector,5)%0x7A == 0 &&
            match(sector,7,new byte[]{0,0,0,0,0}) &&
            (sector[0x0C] > 0 || sector[0x0D] > 0) &&
            DiskPos.isValidTrack(sector[0x0C]) && DiskPos.isValidSector(sector[0x0D]))
        {
            valid = true;
            int ts = 0x0E;
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
        }
        return valid;
    }

//    /**
//     * @param tsmapMapsInCatalog
//     * @throws InvalidPosException
//     */
//    public void findDos33TSMapSectorX(TSMap tsmapMapsInCatalog) throws InvalidPosException
//    {
//        rewind();
//        while (!EOF())
//        {
//            DiskPos cur = this.pos;
//            byte[] sector = read(DiskPos.cSector);
//            if (sector[0]==0 &&
//                    DiskPos.isValidTrack(sector[1]) && DiskPos.isValidSector(sector[2]) &&
//                    sector[3]==0 && sector[4]==0 &&
//                    word(sector,5)%0x7A == 0 &&
//                    match(sector,7,new byte[]{0,0,0,0,0}) &&
//                    (sector[0x0C] > 0 || sector[0x0D] > 0) &&
//                    DiskPos.isValidTrack(sector[0x0C]) && DiskPos.isValidSector(sector[0x0D]))
//                    {
//                        int ts = 0x0E;
//                        boolean valid = true;
//                        while (ts+1 < sector.length && (sector[ts] != 0 || sector[ts+1] != 0) && valid)
//                        {
//                            if (!(DiskPos.isValidTrack(sector[ts]) && DiskPos.isValidSector(sector[ts+1])))
//                            {
//                                valid = false;
//                            }
//                            ts += 2;
//                        }
//                        while (ts < sector.length && valid)
//                        {
//                            if (sector[ts++] != 0)
//                            {
//                                valid = false;
//                            }
//                        }
//                        if (valid)
//                        {
//                            System.out.print("T/S map @ T$"+Integer.toHexString(cur.getTrackInDisk())+", S$"+Integer.toHexString(cur.getSectorInTrack()));
//                            if (sector[1] != 0 || sector[2] != 0)
//                            {
//                                System.out.print(" (next @ T$"+Integer.toHexString(sector[1])+", S$"+Integer.toHexString(sector[2])+")");
//                            }
//                            if (tsmapMapsInCatalog.isMarked(cur.getSectorInDisk()))
//                            {
//                                System.out.print(" (cataloged)");
//                            }
//                            else
//                            {
//                                System.out.print(" (orphaned)");
//                            }
//                            System.out.println();
//                        }
//                    }
//        }
//    }

    /**
     * @param sector
     * @param i
     * @return
     */
    static int word(byte[] sector, int i)
    {
        int lo = sector[i++];
        int hi = sector[i];
        return (lo | (hi << 8)) & 0xFF;
    }

//    /**
//     * @param sector
//     * @param i
//     * @return
//     */
//    static boolean isValidFileName(byte[] sector, int i)
//    {
//        // only check 29 chars of filename (last char could be overwritten by file deletion)
//        for (int x = 0; x < 29; ++x)
//        {
//            if ((sector[i+x] & 0x80) == 0)
//            {
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     * @param b
     * @return
     */
    static boolean isValidFileType(byte b)
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
    static boolean match(byte[] actual, int pos, byte[] expected)
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
