import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
        if (args.length != 1)
        {
            throw new IllegalArgumentException("Usage: java Apple2 dos_3.3_order_disk_image");
        }
        InputStream fileDisk = new FileInputStream(new File(args[0]));
        byte[] rbDisk = new byte[fileDisk.available()];
        fileDisk.read(rbDisk);
    }
}
