import java.util.Collection;

/*
 * Created on Oct 13, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeBoot extends VolumeEntity
{
    private byte[] data;

    private static final String dos80master =
        "01 A5 27 C9 09 D0 18 A5 2B 4A 4A 4A 4A 09 C0 85 "+
        "3F A9 5C 85 3E 18 AD FE 08 6D FF 08 8D FE 08 AE "+
        "FF 08 30 15 BD 4D 08 85 3D CE FF 08 AD FE 08 85 "+
        "27 CE FE 08 A6 2B 6C 3E 00 EE FE 08 EE FE 08 20 "+
        "89 FE 20 93 FE 20 2F FB A6 2B 6C FD 08 00 0D 0B "+
        "09 07 05 03 01 0E 0C 0A 08 06 04 02 0F 00 20 64 "+
        "27 B0 08 A9 00 A8 8D 5D 36 91 40 AD C5 35 4C D2 "+
        "26 AD 5D 36 F0 08 EE BD 35 D0 03 EE BE 35 A9 00 "+
        "8D 5D 36 4C 46 25 8D BC 35 20 A8 26 20 EA 22 4C "+
        "7D 22 A0 13 B1 42 D0 14 C8 C0 17 D0 F7 A0 19 B1 "+
        "42 99 A4 35 C8 C0 1D D0 F6 4C BC 26 A2 FF 8E 5D "+
        "36 D0 F6 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "20 58 FC A9 C2 20 ED FD A9 01 20 DA FD A9 AD 20 "+
        "ED FD A9 00 20 DA FD 60 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 36 09 ";
    private static final String dos83master =
        "01 A5 27 C9 09 D0 18 A5 2B 4A 4A 4A 4A 09 C0 85 "+
        "3F A9 5C 85 3E 18 AD FE 08 6D FF 08 8D FE 08 AE "+
        "FF 08 30 15 BD 4D 08 85 3D CE FF 08 AD FE 08 85 "+
        "27 CE FE 08 A6 2B 6C 3E 00 EE FE 08 EE FE 08 20 "+
        "89 FE 20 93 FE 20 2F FB A6 2B 6C FD 08 00 0D 0B "+
        "09 07 05 03 01 0E 0C 0A 08 06 04 02 0F 00 20 64 "+
        "27 B0 08 A9 00 A8 8D 5D 36 91 40 AD C5 35 4C D2 "+
        "26 AD 5D 36 F0 08 EE BD 35 D0 03 EE BE 35 A9 00 "+
        "8D 5D 36 4C 84 3A 8D BC 35 20 A8 26 20 EA 22 4C "+
        "7D 22 A0 13 B1 42 D0 14 C8 C0 17 D0 F7 A0 19 B1 "+
        "42 99 A4 35 C8 C0 1D D0 F6 4C BB 26 A2 FF 8E 5D "+
        "36 D0 F6 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 36 09 ";
    private static final String dos86master =
        "01 A5 27 C9 09 D0 18 A5 2B 4A 4A 4A 4A 09 C0 85 "+
        "3F A9 5C 85 3E 18 AD FE 08 6D FF 08 8D FE 08 AE "+
        "FF 08 30 15 BD 4D 08 85 3D CE FF 08 AD FE 08 85 "+
        "27 CE FE 08 A6 2B 6C 3E 00 EE FE 08 EE FE 08 20 "+
        "89 FE 20 93 FE 20 2F FB A6 2B 6C FD 08 00 0D 0B "+
        "09 07 05 03 01 0E 0C 0A 08 06 04 02 0F 00 20 64 "+
        "27 B0 08 A9 00 A8 8D 5D 36 91 40 AD C5 35 4C D2 "+
        "26 AD 5D 36 F0 08 EE BD 35 D0 03 EE BE 35 A9 00 "+
        "8D 5D 36 4C B3 36 8D BC 35 20 A8 26 20 EA 22 4C "+
        "7D 22 A0 13 B1 42 D0 14 C8 C0 17 D0 F7 A0 19 B1 "+
        "42 99 A4 35 C8 C0 1D D0 F6 4C BB 26 A2 FF 8E 5D "+
        "36 D0 F6 AD BD 35 8D E6 35 8D EA 35 AD BE 35 8D "+
        "E7 35 8D EB 35 8D E4 35 BA 8E 9B 33 4C 7F 33 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 36 09 ";



    /**
     * @param disk
     */
    public void readFromMedia(Disk disk)
    {
        DiskPos p;
        try
        {
            p = new DiskPos(0,0);
        }
        catch (InvalidPosException e)
        {
            throw new RuntimeException(e);
        }

        rSector.add(new VolumeSector(p,0,this));

        data = disk.readSector(p);
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
        s.append("Boot: ");
        VolumeSector sect = (VolumeSector)rSector.get(0);
        s.append(sect.toString());
        s.append("\n");
    }
    // TODO boot image signature identification

}
