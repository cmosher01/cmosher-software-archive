import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/*
 * Created on Sep 23, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class Hex2Bin
{
    public static byte[] hex2Bin(String hex)
    {
        List r = new ArrayList(hex.length()/2);

        StringTokenizer tok = new StringTokenizer(hex);
        while (tok.hasMoreTokens())
        {
            String shex = tok.nextToken();
            if (shex.length() != 2)
            {
                throw new RuntimeException("hex tokens must be 2 characters in length");
            }
            int n1 = shex.charAt(0);
            int n0 = shex.charAt(1);
            r.add(new Byte((byte)(nib(n1) << 4 | nib(n0));
        }
        byte[] rb = new byte[r.size()];
        int ib = 0;
        for (Iterator i = r.iterator(); i.hasNext();)
        {
            Byte obyte = (Byte)i.next();
            rb[ib++] = obyte.byteValue();
        }
        return rb;
    }
}
