import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
 * Created on Nov 23, 2003
 */

/**
 * @author Chris Mosher
 */
public class ClearHighBit
{
    public static void main(String[] rArg) throws Throwable
    {
    	if (rArg.length != 2)
    	{
    		System.err.println("Usage: java ClearHighBit infile outfile");
    		System.exit(1);
    	}

    	BufferedInputStream in = null;
		BufferedOutputStream out = null;
    	try
    	{
    		in = new BufferedInputStream(new FileInputStream(rArg[0]));
			out = new BufferedOutputStream(new FileOutputStream(rArg[1]));
			for (int n = in.read(); n != -1; n = in.read())
			{
				n &= 0x7f;
				out.write(n);
			}
			out.flush();
    	}
    	finally
    	{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (Throwable ignore)
				{
				}
			}
    		if (in != null)
    		{
    			try
    			{
    				in.close();
    			}
    			catch (Throwable ignore)
    			{
    			}
    		}
    	}
    }
}
