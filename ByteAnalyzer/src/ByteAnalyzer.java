import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/*
 * TODO
 *
 * Created on Jun 15, 2004
 */

/**
 * TODO
 */
public class ByteAnalyzer
{
    private static final int NORMAL = 0;
    private static final int CR = 1;
    private static final int LF = 2;

    private Map cBytes = new TreeMap();
    private int cCRLF;
    private int cLFCR;

    private int state;

    public static void main(String[] args) throws IOException
    {
        if (args.length != 1)
        {
            throw new IllegalArgumentException("Usage: java ByteAnalyzer input-file");
        }
        ByteAnalyzer prog = new ByteAnalyzer();
        prog.execute(new File(args[0]));
    }

    public void execute(File f) throws IOException
    {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
        int x = in.read();
        while (x != -1)
        {
            switch (state)
            {
                case NORMAL:
                {
                    if (x == 0x0d)
                    {
                        state = CR;
                    }
                    else if (x == 0x0a)
                    {
                        state = LF;
                    }
                    else
                    {
                        incByte(x);
                    }
                }
                break;
                case CR:
                {
                    if (x == 0x0a)
                    {
                        ++cCRLF;
                        state = NORMAL;
                    }
                    else if (x == 0x0d)
                    {
                        incByte(0x0d);
                    }
                    else
                    {
                        incByte(0x0d);
                        incByte(x);
                        state = NORMAL;
                    }
                }
                break;
                case LF:
                {
                    if (x == 0x0d)
                    {
                        ++cLFCR;
                        state = NORMAL;
                    }
                    else if (x == 0x0a)
                    {
                        incByte(0x0a);
                    }
                    else
                    {
                        incByte(0x0a);
                        incByte(x);
                        state = NORMAL;
                    }
                }
                break;
            }
            x = in.read();
        }
        in.close();
        showMap();
    }

    private void incByte(int x)
    {
        Integer i = new Integer(x);
        if (!cBytes.containsKey(i))
        {
            cBytes.put(i,new Integer(1));
        }
        else
        {
            int cx = ((Integer)cBytes.get(i)).intValue();
            cBytes.put(i,new Integer(++cx));
        }
    }

    public void showMap()
    {
//        if (cCRLF > 0)
//        {
//            System.out.println("CRLF  : "+cCRLF);
//        }
//        if (cLFCR > 0)
//        {
//            System.out.println("LFCR  : "+cLFCR);
//        }
        for (Iterator i = cBytes.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry)i.next();
            int byt = ((Integer)entry.getKey()).intValue();
            int cnt = ((Integer)entry.getValue()).intValue();
            if (byt >= 0x80 && cnt > 0)
            {
                String s = Integer.toHexString(byt);
                if (s.length() == 1)
                {
                    s = "0"+s;
                }
                System.out.print(s);
                System.out.print(" ");
                if ('!' <= byt && byt <= '~')
                {
                    char c = (char)byt;
                    System.out.print('\'');
                    System.out.print(c);
                    System.out.print('\'');
                }
                else
                {
                    System.out.print("   ");
                }
                System.out.print(": ");
                System.out.println(cnt);
            }
        }
    }
}
