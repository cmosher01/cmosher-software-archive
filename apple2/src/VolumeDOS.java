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
            pLim = new DiskPos(3,0);
            p = new DiskPos(0,1);
        }
        catch (InvalidPosException e)
        {
            throw new RuntimeException(e);
        }
        while (!p.equals(pLim))
        {
            rPosDOS.add(p);
            p.advance(DiskPos.cSector);
        }
    }
    /**
     * @param disk
     */
    public void readFromMedia(Disk disk)
    {
        int i = 0;
        try
        {
            rSector.add(new VolumeSector(new DiskPos(0,1),i++));
            rSector.add(new VolumeSector(new DiskPos(0,2),i++));
            rSector.add(new VolumeSector(new DiskPos(0,3),i++));
            rSector.add(new VolumeSector(new DiskPos(0,4),i++));
            rSector.add(new VolumeSector(new DiskPos(0,5),i++));
            rSector.add(new VolumeSector(new DiskPos(0,6),i++));
            rSector.add(new VolumeSector(new DiskPos(0,7),i++));
            rSector.add(new VolumeSector(new DiskPos(0,8),i++));
            rSector.add(new VolumeSector(new DiskPos(0,9),i++));
            rSector.add(new VolumeSector(new DiskPos(0,10),i++));
            rSector.add(new VolumeSector(new DiskPos(0,11),i++));
            rSector.add(new VolumeSector(new DiskPos(0,12),i++));
            rSector.add(new VolumeSector(new DiskPos(0,13),i++));
            rSector.add(new VolumeSector(new DiskPos(0,14),i++));
            rSector.add(new VolumeSector(new DiskPos(0,15),i++));
            rSector.add(new VolumeSector(new DiskPos(1,0),i++));
            rSector.add(new VolumeSector(new DiskPos(1,1),i++));
            rSector.add(new VolumeSector(new DiskPos(1,2),i++));
            rSector.add(new VolumeSector(new DiskPos(1,3),i++));
            rSector.add(new VolumeSector(new DiskPos(1,4),i++));
            rSector.add(new VolumeSector(new DiskPos(1,5),i++));
            rSector.add(new VolumeSector(new DiskPos(1,6),i++));
            rSector.add(new VolumeSector(new DiskPos(1,7),i++));
            rSector.add(new VolumeSector(new DiskPos(1,8),i++));
            rSector.add(new VolumeSector(new DiskPos(1,9),i++));
            rSector.add(new VolumeSector(new DiskPos(1,10),i++));
            rSector.add(new VolumeSector(new DiskPos(1,11),i++));
            rSector.add(new VolumeSector(new DiskPos(1,12),i++));
            rSector.add(new VolumeSector(new DiskPos(1,13),i++));
            rSector.add(new VolumeSector(new DiskPos(1,14),i++));
            rSector.add(new VolumeSector(new DiskPos(1,15),i++));
            rSector.add(new VolumeSector(new DiskPos(2,0),i++));
            rSector.add(new VolumeSector(new DiskPos(2,1),i++));
            rSector.add(new VolumeSector(new DiskPos(2,2),i++));
            rSector.add(new VolumeSector(new DiskPos(2,3),i++));
            rSector.add(new VolumeSector(new DiskPos(2,4),i++));
            rSector.add(new VolumeSector(new DiskPos(2,5),i++));
            rSector.add(new VolumeSector(new DiskPos(2,6),i++));
            rSector.add(new VolumeSector(new DiskPos(2,7),i++));
            rSector.add(new VolumeSector(new DiskPos(2,8),i++));
            rSector.add(new VolumeSector(new DiskPos(2,9),i++));
            rSector.add(new VolumeSector(new DiskPos(2,10),i++));
            rSector.add(new VolumeSector(new DiskPos(2,11),i++));
            rSector.add(new VolumeSector(new DiskPos(2,12),i++));
            rSector.add(new VolumeSector(new DiskPos(2,13),i++));
            rSector.add(new VolumeSector(new DiskPos(2,14),i++));
            rSector.add(new VolumeSector(new DiskPos(2,15),i++));
        }
        catch (InvalidPosException e)
        {
            throw new RuntimeException(e);
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
        if (hasDaviddosSignature())
        {
            s.append(" (David DOS)");
        }
        if (hasDaviddos2Signature())
        {
            s.append(" (David DOS II)");
        }
        if (hasDiversidos2cSignature())
        {
            s.append(" (Diversi-DOS 2-C)");
        }
        if (hasDiversidos41cSignature())
        {
            s.append(" (Diversi-DOS 4.1-C)");
        }
        if (hasEsdosSignature())
        {
            s.append(" (ES DOS)");
        }
        if (hasHyperdosSignature())
        {
            s.append(" (Hyper-DOS)");
        }
        if (this.hasProtodosSignature())
        {
            s.append(" (Pronto-DOS [Beagle Bros.])");
        }
        if (this.hasRdosSignature())
        {
            s.append(" (RDOS [SSI])");
        }
        // TODO various DOS 3.3 signatures
        // TODO Franklin signature
        // TODO AMDOS signature
    }

    /**
     * @param knownSectors
     * @return
     */
    public static boolean isDOSKnown(Collection knownSectors)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
