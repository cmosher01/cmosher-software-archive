import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Dump
{
    private final static int BPL = 32;
    private final InputStream mFile;
    private final int[] mLine = new int[BPL];
    private int cLine;

    private static final int END_OF_FILE = -1;

    public static void main(String[] args) throws Exception
    {
        InputStream ins;
        if (args.length == 0)
        {
            ins = System.in;
        }
        else
        {
            File f = new File(args[0]);
            ins = new FileInputStream(f);
        }

        Dump d = new Dump(ins);
        d.dump();
    }

    public Dump(InputStream file)
    {
        mFile = file;
    }

    public void dump() throws IOException
    {
        readLine();
        int base = 0;
        while (cLine > 0)
        {
            StringBuffer sb = new StringBuffer(8+2+(16*3)+16);
            toHex(base,8,sb);
            sb.append(": ");
            for (int i = 0; i < BPL; ++i)
            {
                if (i < cLine)
                {
                    toHex(mLine[i],2,sb);
                    sb.append(" ");
                }
                else
                {
                    sb.append("   ");
                }
            }
            for (int i = 0; i < BPL; ++i)
            {
                if (i < cLine)
                {
                    toChar(mLine[i],sb);
                }
                else
                {
                    sb.append(" ");
                }
            }
            System.out.println(sb);
            base += BPL;
            readLine();
        }
    }

    protected void toChar(int c, StringBuffer sb)
    {
        c &= 0x7f;
        if (c==0x00 || c==0x7f)
            c = ' ';
        else if (c < ' ')
            c |= 0x40;
        sb.append((char)c);
    }

    protected void readLine() throws IOException
    {
        for (cLine = 0; cLine < BPL; ++cLine)
        {
            int c = mFile.read();
            if (c == END_OF_FILE)
                break;

            assert 0x00 <= c && c <= 0xFF : "bad byte value: "+c;

            mLine[cLine] = c;
        }
    }

    protected void toHex(int i, int minLen, StringBuffer s)
    {
        String hex = Integer.toHexString(i);
        rep('0',minLen-hex.length(),s);
        s.append(hex);
    }

    protected void rep(char c, int len, StringBuffer s)
    {
        for (int i = 0; i < len; ++i)
            s.append(c);
    }
}
