import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*
 * Created on Sep 17, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class Apple2
{
    /**
     * @param args
     * @throws InvalidPosException
     * @throws IOException
     */
    public static void main(String[] args) throws InvalidPosException, IOException
    {
//        mainAll(args);
//        doOneDisk(readDisk(new File("test/SARG2.DSK")));
        doOneDisk(readDisk(new File("test/DOS33_SystemMaster_19800825.dsk")));
//        doOneDisk(readDisk(new File("test/montezuma.dsk")));
//        doOneDisk(readDisk(new File("test/DiversiDOS_2_C_1982.dsk")));
    }

    /**
     * @param args
     * @throws InvalidPosException
     * @throws IOException
     */
    public static void mainAll(String[] args) throws InvalidPosException, IOException
    {
        if (args.length != 1)
        {
            throw new IllegalArgumentException("Usage: java Apple2 dirtree_of_dos_3.3_order_disk_images");
        }
        File[] rf = list140KFiles(args[0]);
        for (int i = 0; i < rf.length; i++)
        {
            System.out.println(rf[i].getAbsolutePath());
            doOneDisk(readDisk(rf[i]));
        }
    }

    /**
     * @param disk
     * @throws InvalidPosException
     */
    public static void doOneDisk(Disk disk) throws InvalidPosException
    {
        Volume vol = new Volume();
        try
        {
            vol.readFromMedia(disk);
        }
        catch (VTOCNotFoundException e)
        {
            System.out.println("[no VTOC]");
            return;
        }
        catch (MultipleVTOCException e)
        {
            System.out.println("[no VTOC]");
            return;
        }

        StringBuffer s = new StringBuffer(256);
        vol.dump(s);
        System.out.println(s);

//        Map mapDiskMap = new HashMap();

//        List rUsedCat = new ArrayList();
//        vol.getUsedCatalog(rUsedCat);


        //        mapDiskMap.put(p, new VolumeTableOfContents(p));


//        List rPosCat = new ArrayList();
//        disk.findDos33CatalogSector(rPosCat);
//
//        if (rPosCat.size() == 0)
//        {
//            System.out.println("    [no files found]");
//            return;
//        }
//
//        List rCatEntry = new ArrayList();
//        for (Iterator i = rPosCat.iterator(); i.hasNext();)
//        {
//            DiskPos p = (DiskPos)i.next();
//            mapDiskMap.put(p, new VolumeCatalog(p));
//            disk.getDos33CatalogEntries(p, rCatEntry);
//        }
//        for (Iterator ent = rCatEntry.iterator(); ent.hasNext();)
//        {
//            Dos33CatalogEntry entry = (Dos33CatalogEntry)ent.next();
//            System.out.println("    "+entry.getName());
//        }
//        List rCat = new ArrayList();
//        disk.findDos33CatalogSector(rCat);
//
//        List rTSMap = new ArrayList();
//        disk.findDos33TSMapSector(rTSMap);
    }

    /**
     * Reads a disk image from a file.
     * 
     * @param f file containing the disk image to read
     * @return the disk image read in from the file
     * @throws IOException
     */
    public static Disk readDisk(File f) throws IOException
    {
        byte[] rbDisk;
        InputStream fileDisk = null;
        try
        {
            fileDisk = new FileInputStream(f);
            rbDisk = new byte[fileDisk.available()];
            fileDisk.read(rbDisk);
        }
        finally
        {
            if (fileDisk != null)
            {
                try
                {
                    fileDisk.close();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }

        return new Disk(rbDisk);
    }

    /**
     * @param sDir
     * @return
     * @throws IOException
     */
    public static File[] list140KFiles(String sDir) throws IOException
    {
        final List dirs = new ArrayList();
        dirs.add(new File(sDir));

        List results = new ArrayList();

        while (dirs.size() > 0)
        {
            File d = (File)dirs.remove(0);
            File[] rm = d.listFiles(new FileFilter()
            {
                public boolean accept(File pathname)
                {
                    boolean accept = false;
                    if (pathname.isDirectory())
                    {
                        dirs.add(pathname);
                    }
                    else
                    {
                        accept = pathname.length() == 0x23000;
                    }
                    return accept;
                }
            });
            for (int i = 0; rm != null && i < rm.length; ++i)
            {
                File file = rm[i];
                results.add(file.getCanonicalFile().getAbsoluteFile());
            }
        }
        return (File[])results.toArray(new File[results.size()]);
    }
}
