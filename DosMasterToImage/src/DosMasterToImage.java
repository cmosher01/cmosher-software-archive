import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * Created on Nov 2, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class DosMasterToImage
{

    public static void main(String[] args) throws IOException
    {
        if (args.length != 2)
        {
            throw new IllegalArgumentException("Usage: java DosMasterToImage master-image disk-image");
        }

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(args[0])));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(args[1])));

        if (in.available() != 0xC000-0x9B00)
        {
            throw new IllegalArgumentException("Input file length must be "+(0xC000-0x9B00)+" bytes.");
        }
    }
}
