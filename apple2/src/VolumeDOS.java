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

    private static List rPosDOS = new ArrayList();
    static
    {
        DiskPos p, pLim;
        try
        {
            p = new DiskPos(0,1);
            pLim = new DiskPos(3,0);
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
    public boolean hasProtodosSignature()
    {
        return (rb[0x1602-0x100] == 0x54 && rb[0x1603-0x100] == 0x59 && rb[0x1604-0x100] == 0x50 && rb[0x1605-0x100] == 0xFFFFFFC5);
    }
    /**
     * @return
     */
    public boolean hasDaviddosSignature()
    {
        return (rb[0x1924-0x100] == 0x44 && rb[0x1925-0x100] == 0x2D && rb[0x1926-0x100] == 0x44 && rb[0x1927-0x100] == 0x4F && rb[0x1928-0x100] == 0x53);
    }
    /**
     * @return
     */
    public boolean hasDaviddos2Signature()
    {
        return hasDaviddosSignature() && 
        (rb[0x2701-0x100] == 0xFFFFFFC4 && rb[0x2702-0x100] == 0xFFFFFFC1 && rb[0x2703-0x100] == 0xFFFFFFD6 && rb[0x270B-0x100] == 0xFFFFFFC9 && rb[0x270C-0x100] == 0xFFFFFFC9);
    }
    /**
     * @return
     */
    public boolean hasDiversidos2cSignature()
    {
        return (rb[0xA10-0x100] == 0x20 && rb[0xA11-0x100] == (byte)0x84 && rb[0xA12-0x100] == 0xFFFFFF9D && rb[0xA13-0x100] == 0xFFFFFFA0);
    }
    /**
     * @return
     */
    public boolean hasDiversidos41cSignature()
    {
        return (rb[0xA10-0x100] == 0xFFFFFFA9 && rb[0xA11-0x100] == 0xFFFFFFFF && rb[0xA12-0x100] == 0xFFFFFF8D && rb[0xA13-0x100] == 0xFFFFFFFB);
    }
    /**
     * @return
     */
    public boolean hasEsdosSignature()
    {
        return (rb[0x1671-0x100] == 0x4C && rb[0x1672-0x100] == 0x4E && rb[0x1673-0x100] == 0xFFFFFFC1 && rb[0x1674-0x100] == 0x52);
    }
    /**
     * @return
     */
    public boolean hasHyperdosSignature()
    {
        return (rb[0x656-0x100] == 0xFFFFFFAD && rb[0x657-0x100] ==  0x61 && rb[0x658-0x100] ==  0xFFFFFFAA && rb[0x659-0x100] ==  0xFFFFFFC9 && rb[0x65A-0x100] ==  0x01 && rb[0x65B-0x100] ==  0xFFFFFFB0);
    }
    /**
     * @return
     */
    public boolean hasRdosSignature()
    {
        return (rb[0x100-0x100] == 0x4C && rb[0x101-0x100] ==  0x74 && rb[0x102-0x100] ==  0xFFFFFFB9 && rb[0x103-0x100] ==  0xFFFFFFA0);
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
        if (this.hasProtodosSignature())
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
            short addr = (short)(rb[0x84]+rb[0x85]<<8);
            
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
