import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/*
 * Created on Oct 13, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeDOS extends VolumeEntity
{
    private byte[] rb;
    private byte[] rbCmp;

    private static final int[] rIgnore1980 = {  0x00B4D, 0x00B58, 0x00B78, 0x00B79, 0x00B7B, 0x00B7B, 0x00B7D, 0x00BFF, 0x00D42,
            0x00D42, 0x0194F, 0x019B7, 0x02297, 0x024FF, 0x000B3, 0x000FF, 0x0018D, 0x00192, 0x001DF, 0x001FF, 0x003FD, 0x003FF,
            0x00469, 0x00495, 0x00500, 0x00655, 0x006DF, 0x006FF, 0x009A8, 0x009B7, 0x009D9, 0x009DB};

    private static final int[] rIgnore1983 = {  0x00B4D, 0x00B58, 0x00B7C, 0x00B7D, 0x00B7F, 0x00B7F, 0x00B81, 0x00BFF, 0x00D42,
            0x00D42, 0x0194F, 0x019B7, 0x0225D, 0x0225E, 0x02297, 0x024FF, 0x000B3, 0x000FF, 0x0018D, 0x00192, 0x001DF, 0x001FF,
            0x003FD, 0x003FF, 0x00494, 0x00495, 0x00500, 0x00655, 0x006DF, 0x006FF, 0x009A8, 0x009B7};

    private static final int[] rIgnore1986 = {  0x00B4D, 0x00B58, 0x00B7C, 0x00B7D, 0x00B7F, 0x00B7F, 0x00B81, 0x00BFF, 0x00D42,
            0x00D42, 0x0194F, 0x019B7, 0x0225D, 0x0225E, 0x02297, 0x024FF, 0x000CF, 0x000FF, 0x0018D, 0x00192, 0x001DF, 0x001FF,
            0x003FD, 0x003FF, 0x00484, 0x00495, 0x00500, 0x00655, 0x006DF, 0x006FF, 0x009A8, 0x009B7};

    private static List rPosDOS = new ArrayList();
    private static int[] rbClear1980 = new int[0x4000-0x1B00];
    private static int[] rbClear1983 = new int[0x4000-0x1B00];
    private static int[] rbClear1986 = new int[0x4000-0x1B00];
    static
    {
        DiskPos p, pLim;
        try
        {
            p = new DiskPos(0,0);
            pLim = new DiskPos(2,5);
        }
        catch (InvalidPosException e)
        {
            throw new RuntimeException(e);
        }
        while (!p.equals(pLim))
        {
            rPosDOS.add(p.clone());
            p.advance(DiskPos.cSector);
        }

        try
        {
            BufferedInputStream in = new BufferedInputStream(VolumeDOS.class.getClassLoader().getResourceAsStream("dos33_1980_clear.bin"));
            int x = 0;
            int b = in.read();
            while (b != -1)
            {
                rbClear1980[x++] = b;
                b = in.read();
            }
            in.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        try
        {
            BufferedInputStream in = new BufferedInputStream(VolumeDOS.class.getClassLoader().getResourceAsStream("dos33_1983_clear.bin"));
            int x = 0;
            int b = in.read();
            while (b != -1)
            {
                rbClear1983[x++] = b;
                b = in.read();
            }
            in.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        try
        {
            BufferedInputStream in = new BufferedInputStream(VolumeDOS.class.getClassLoader().getResourceAsStream("dos33_1986_clear.bin"));
            int x = 0;
            int b = in.read();
            while (b != -1)
            {
                rbClear1986[x++] = b;
                b = in.read();
            }
            in.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param disk
     */
    public void readFromMedia(Disk disk)
    {
        int x = 0;
        for (Iterator i = rPosDOS.iterator(); i.hasNext();)
        {
            DiskPos p = (DiskPos)i.next();
            rSector.add(new VolumeSector(p,x++,this));
        }

        List rPos = new ArrayList();
        getUsed(rPos);
        rb = disk.getDos33File(rPos);
        rbCmp = new byte[rb.length];
        System.arraycopy(rb, 0, rbCmp, 0, rb.length);
    }

    /**
     * @param rPos
     */
    public void getUsed(Collection rPos)
    {
        getPos(rPos);
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        s.append("DOS");
        appendSig(s);
        s.append(": ");
        for (Iterator i = this.rSector.iterator(); i.hasNext();)
        {
            VolumeSector sect = (VolumeSector)i.next();
            s.append(sect.toString());
            if (i.hasNext())
            {
                s.append("; ");
            }
        }
        s.append("\n");
    }

    /**
     * @return
     */
    public boolean hasProntodosSignature()
    {
        return (rb[0x1602] == 0x54 && rb[0x1603] == 0x59 && rb[0x1604] == 0x50 && rb[0x1605] == 0xFFFFFFC5);
    }
    /**
     * @return
     */
    public boolean hasDaviddosSignature()
    {
        return (rb[0x1924] == 0x44 && rb[0x1925] == 0x2D && rb[0x1926] == 0x44 && rb[0x1927] == 0x4F && rb[0x1928] == 0x53);
    }
    /**
     * @return
     */
    public boolean hasDaviddos2Signature()
    {
//        return hasDaviddosSignature() && 
//        (rb[0x2701] == 0xFFFFFFC4 && rb[0x2702] == 0xFFFFFFC1 && rb[0x2703] == 0xFFFFFFD6 && rb[0x270B] == 0xFFFFFFC9 && rb[0x270C] == 0xFFFFFFC9);
        // TODO fix daviddos2
        return false;
    }
    /**
     * @return
     */
    public boolean hasDiversidos2cSignature()
    {
        return (rb[0xA10] == 0x20 && rb[0xA11] == (byte)0x84 && rb[0xA12] == 0xFFFFFF9D && rb[0xA13] == 0xFFFFFFA0);
    }
    /**
     * @return
     */
    public boolean hasDiversidos41cSignature()
    {
        return (rb[0xA10] == 0xFFFFFFA9 && rb[0xA11] == 0xFFFFFFFF && rb[0xA12] == 0xFFFFFF8D && rb[0xA13] == 0xFFFFFFFB);
    }
    /**
     * @return
     */
    public boolean hasEsdosSignature()
    {
        return (rb[0x1671] == 0x4C && rb[0x1672] == 0x4E && rb[0x1673] == 0xFFFFFFC1 && rb[0x1674] == 0x52);
    }
    /**
     * @return
     */
    public boolean hasHyperdosSignature()
    {
        return (rb[0x656] == 0xFFFFFFAD && rb[0x657] ==  0x61 && rb[0x658] ==  0xFFFFFFAA && rb[0x659] ==  0xFFFFFFC9 && rb[0x65A] ==  0x01 && rb[0x65B] ==  0xFFFFFFB0);
    }
    /**
     * @return
     */
    public boolean hasRdosSignature()
    {
        return (rb[0x100] == 0x4C && rb[0x101] ==  0x74 && rb[0x102] ==  0xFFFFFFB9 && rb[0x103] ==  0xFFFFFFA0);
    }

    /**
     * @param s
     */
    public void appendSig(StringBuffer s)
    {
        boolean alt = false;
        if (hasDaviddosSignature())
        {
            s.append(" (David DOS)");
            alt = true;
        }
        if (hasDaviddos2Signature())
        {
            s.append(" (David DOS II)");
            alt = true;
        }
        if (hasDiversidos2cSignature())
        {
            s.append(" (Diversi-DOS 2-C)");
            alt = true;
        }
        if (hasDiversidos41cSignature())
        {
            s.append(" (Diversi-DOS 4.1-C)");
            alt = true;
        }
        if (hasEsdosSignature())
        {
            s.append(" (ES DOS)");
            alt = true;
        }
        if (hasHyperdosSignature())
        {
            s.append(" (Hyper-DOS)");
            alt = true;
        }
        if (this.hasProntodosSignature())
        {
            s.append(" (Pronto-DOS [Beagle Bros.])");
            alt = true;
        }
        if (this.hasRdosSignature())
        {
            s.append(" (RDOS [SSI])");
            alt = true;
        }
        // TODO AMDOS signature
        // TODO Franklin signature

        // various DOS 3.3 signatures
        if (!alt)
        {
            /*
             * Assume is normal DOS 3.3, so find out which
             * version (1980, 1983, or 1986).
             */
            int tempdostype = 0;
            int x = rb[0x84] & 0xFF;
            if (x == 0x46)
            {
                tempdostype = 1980;
                clearIgnored(rIgnore1980);
                if (cmpDOS(rbClear1980))
                {
                    s.append(" (DOS 3.3 1980 exact match)");
                }
                else
                {
                    s.append(" (DOS 3.3 1980 altered)");
                }
            }
            else if (x == 0x84)
            {
                tempdostype = 1983;
                clearIgnored(rIgnore1983);
                if (cmpDOS(rbClear1983))
                {
                    s.append(" (DOS 3.3 1983 exact match)");
                }
                else
                {
                    s.append(" (DOS 3.3 1983 altered)");
                }
            }
            else if (x == 0xB3)
            {
                tempdostype = 1986;
                clearIgnored(rIgnore1986);
                if (cmpDOS(rbClear1986))
                {
                    s.append(" (DOS 3.3 1986 exact match)");
                }
                else
                {
                    s.append(" (DOS 3.3 1986 altered)");
                }
            }
        }
    }

    /**
     * @param rbClear
     */
    private boolean cmpDOS(int[] rbClear)
    {
        if (rbCmp.length != rbClear.length)
        {
            throw new RuntimeException("DOS array lengths don't match");
        }
        for (int i = 0; i < rbClear.length; ++i)
        {
            if (rbCmp[i] != rbClear[i])
            {
                return false;
            }
        }
        return true;
    }

    /**
     * @param rIgn
     */
    private void clearIgnored(int[] rIgn)
    {
        for (int i = 0; i < rIgn.length/2; ++i)
        {
            for (int b = rIgn[i*2]; b <= rIgn[i*2+1]; ++b)
            {
                rbCmp[b] = 0;
            }
        }
    }

    /**
     * @param knownSectors
     * @return
     */
    public static boolean isDOSKnown(Collection knownSectors)
    {
        int c = 0x1f; // check T$00,S$1 thru T$01,S$F only (e.g. Prontodos)
        for (Iterator i = rPosDOS.iterator(); i.hasNext() && c-- > 0;)
        {
            DiskPos p = (DiskPos)i.next();
            if (knownSectors.contains(p))
            {
                return true;
            }
        }
        return false;
    }
}
