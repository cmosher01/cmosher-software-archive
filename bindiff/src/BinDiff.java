import java.io.IOException;
import java.io.RandomAccessFile;

/*
 * Created on Nov 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class BinDiff
{
    public static void main(String[] rArg) throws Throwable
    {
    	if (rArg.length != 2)
    	{
    		System.err.println("Usage: java BinDiff file1 file2");
    		System.exit(1);
    	}

		RandomAccessFile f1 = new RandomAccessFile(rArg[0],"r");
		RandomAccessFile f2 = new RandomAccessFile(rArg[1],"r");

		BinDiff d = new BinDiff(f1,f2,2,64);
		d.diff();

		f2.close();
		f1.close();
    }

    private static final int START = 0;
    private static final int COPY = 1;
    private static final int SKIP = 2;
    private static final int INSERT = 3;
    private static final int END = 4;

    private static final int EOF = -1;

    private final PushbackRandomFile f1;
    private final PushbackRandomFile f2;
    private final int cMinMatch;
    private final int cMaxSearch;

	private int state;
	private int ccopy;
	private int cskip;
	private int cinsert;
	private long posinsert = -1;
	private PushbackRandomFile fileinsert;
	private long lastmark;

    public BinDiff(RandomAccessFile f1, RandomAccessFile f2, int cMinMatch, int cMaxSearch)
    {
        this.f1 = new PushbackRandomFile(f1);
        this.f2 = new PushbackRandomFile(f2);
        this.cMinMatch = cMinMatch;
        this.cMaxSearch = cMaxSearch;
    }

    public void diff() throws IOException
    {
        statechange(START,0);
        int c1 = f1.read();
        int c2 = f2.read();
        while ((c1 != EOF) || (c2 != EOF))
        {
            if (c1 == c2)
            {
                statechange(COPY,1);
            }
            else
            {
                f1.unread(c1);
                f2.unread(c2);

                if (difFindMatch())
                {
                    statechange(INSERT,lastmark-f2.tell(),f2);
                }
                else if (difFindMatch())
                {
                    statechange(SKIP,lastmark-f1.tell(),f1);
                }
                else
                {
                    statechange(SKIP,1,f1);
                    statechange(INSERT,1,f2);
                }
            }
            c1 = f1.read();
            c2 = f2.read();
        }
        statechange(END,0);
    }

	protected void statechange(int newstate, long c) throws IOException
	{
		statechange(newstate,c,null);
	}

    protected void statechange(int newstate, long c, PushbackRandomFile f) throws IOException
    {
        if (newstate == START)
        {
            state = START;
            return;
        }

		StringBuffer s = new StringBuffer(256);
        if (newstate != state)
        {
            if (state == COPY)
            {
            	s.append(">>>COPY ");
            	s.append(ccopy);
            	s.append(" BYTES\n");

				ccopy = 0;
            }
            else if (state == SKIP || state == INSERT)
            {
                if (newstate == COPY || newstate == END)
                {
                    if (cskip > 0)
                    {
						s.append(">>>DELETE ");
						s.append(cskip);
						s.append(" BYTES\n");

                        cskip = 0;
                    }
                    if (cinsert > 0)
                    {
						s.append(">>>INSERT ");
						s.append(cinsert);
						s.append(" BYTES\n");

                        long orig = fileinsert.tell();

                        fileinsert.seek(posinsert);
                        for (int i = 0; i < cinsert; ++i)
                        {
							//fputc(fgetc(fileinsert), fdif);
                        }

                        fileinsert.seek(orig);

                        cinsert = 0;
                        posinsert = -1;
                    }
                }
            }
            state = newstate;
        }

        switch (state)
        {
            case COPY:
                ccopy += c;
            break;
            case SKIP:
                cskip += c;
                for (int i = 0; i < c; ++i)
                {
                	f.read();
                }
            break;
            case INSERT:
                if (posinsert < 0)
                {
                    posinsert = f.tell();
                    fileinsert = f;
                }
                cinsert += c;
				for (int i = 0; i < c; ++i)
				{
					f.read();
				}
            break;
        }
    }

	protected boolean  difFindMatch() throws IOException
    {
        long orig = f2.tell();

        boolean endoffile = false;
        int cs = cMaxSearch;
        while ((cs-- > 0) && !endoffile && !difMatch())
        {
			endoffile = (f2.read() == EOF);
        }

        boolean found = (!endoffile && (cs > 0));

        lastmark = f2.tell();

        f2.seek(orig);

        return found;
    }

    protected boolean difMatch() throws IOException
    {
        long orig1 = f1.tell();
		long orig2 = f2.tell();

        boolean same = true;
        for (int i = 0; i < cMinMatch && same; ++i)
        {
            int c1 = f1.read();
            int c2 = f2.read();
            if ((c2 == EOF && c1 != EOF) || (c1 != c2))
            {
				same = false;
            }
        }

		f1.seek(orig1);
		f2.seek(orig2);

        return same;
    }
}
