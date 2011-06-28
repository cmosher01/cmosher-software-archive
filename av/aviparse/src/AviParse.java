import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
 * Created on Aug 10, 2004
 */

/**
 * @author Chris Mosher
 */
public class AviParse
{
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        ListChunk riff = null;
        RiffInputStream in = null;

        try
        {
            in = new RiffInputStream(new BufferedInputStream(new FileInputStream(new File(args[0]))));

            riff = (ListChunk)in.readChunk(new Remainder(in.available()));
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }
        riff.pp();
    }
}
