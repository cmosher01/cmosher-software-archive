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
            r.add(new Byte((byte)(nib(n1) << 4 | nib(n0))));
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

    /**
     * @param n0
     * @return
     */
    private static int nib(int n)
    {
        if ('0' <= n && n <= '9')
        {
            return n - '0';
        }
        if ('A' <= n && n <= 'F')
        {
            return 10 - 'A' + n;
        }
        if ('a' <= n && n <= 'f')
        {
            return 10 - 'a' + n;
        }
        throw new RuntimeException("invalid hex character");
    }

    public static String hexbyte(byte x)
    {
        int i;
        if (x < 0)
        {
            i = 256+x;
        }
        else
        {
            i = x;
        }
        String s = Integer.toHexString(i);
        s = s.toUpperCase();
        if (s.length() == 1)
        {
            s = "0"+x;
        }
        return s;
    }
}
