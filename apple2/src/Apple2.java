import java.io.File;
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
     * @throws IOException
     * @throws InvalidPosException
     */
    public static void main(String[] args) throws IOException, InvalidPosException
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
        List rTSMap = new ArrayList()
        disk.findDos33TSMapSector(tTSMap);
    }
}
