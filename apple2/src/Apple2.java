import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        mainAll(args);
    }

    public static void mainAll(String[] args) throws IOException
    {
        if (args.length != 1)
        {
            throw new IllegalArgumentException("Usage: java Apple2 dos_3.3_order_disk_image");
        }
    }

    public static void mainOne(String[] args) throws IOException
    {
        if (args.length != 1)
        {
            throw new IllegalArgumentException("Usage: java Apple2 dos_3.3_order_disk_image");
        }

        InputStream fileDisk = new FileInputStream(new File(args[0]));
        byte[] rbDisk = new byte[fileDisk.available()];
        fileDisk.read(rbDisk);

        Disk disk = new Disk(rbDisk);

        List rVTOC = new ArrayList();
        disk.findDos33VTOC(rVTOC);

        List rCat = new ArrayList();
        disk.findDos33CatalogSector(rCat);

        List rTSMap = new ArrayList();
        disk.findDos33TSMapSector(rTSMap);
    }

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
                    return pathname.length() == 0x23000;
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
