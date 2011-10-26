import java.io.File;
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

		File fil1 = new File(rArg[0]);
		File fil2 = new File(rArg[1]);
		RandomAccessFile f1 = new RandomAccessFile(fil1,"r");
		RandomAccessFile f2 = new RandomAccessFile(fil2,"r");

		System.out.println("file 1: "+fil1.getAbsolutePath());
		System.out.println("file 2: "+fil2.getAbsolutePath());

		BinDiff d = new BinDiff(f1,f2,4,512);
		d.diff();

		f2.close();
		f1.close();

		System.in.read();
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
	private long lastmark;
	private int ccopy;
	private int cskip;
	private int cinsert;
	private long poscopy = -1;
	private long posskip = -1;
	private long posinsert = -1;
	private PushbackRandomFile filecopy;
	private PushbackRandomFile fileskip;
	private PushbackRandomFile fileinsert;
	private DiffWriter dw = new DiffWriter();

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
                statechange(COPY,1,f1);
            }
            else
            {
                f1.unread(c1);
                f2.unread(c2);

//                if (difFindMatch(false))
//                {
//                    statechange(INSERT,lastmark-f2.tell(),f2);
//                }
//                else if (difFindMatch(true))
//                {
//                    statechange(SKIP,lastmark-f1.tell(),f1);
//                }
//                else
                {
                    statechange(SKIP,1,f1);
                    statechange(INSERT,1,f2);
                }
            }
            c1 = f1.read();
            c2 = f2.read();
        }
        statechange(END,0);
        dw.flush();
    }

	protected void outBytes(int method, PushbackRandomFile f, long pos, int len) throws IOException
	{
		String s = "";
		switch (method)
		{
			case 0:
				s = "COPY";
			break;
			case 1:
				s = "DELETE";
			break;
			case 2:
				s = "INSERT";
			break;
		}
		dw.outTag(s,len);

		long orig = f.tell();

		f.seek(pos);
		dw.beginBlock();
		for (int i = 0; i < len; ++i)
		{
			dw.outByte(f.read());
		}
		dw.endBlock();

		f.seek(orig);
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

        if (newstate != state)
        {
            if (state == COPY)
            {
				outBytes(0,filecopy,poscopy,ccopy);
				poscopy = -1;
				ccopy = 0;
            }
            else if (state == SKIP || state == INSERT)
            {
                if (newstate == COPY || newstate == END)
                {
                    if (cskip > 0)
                    {
						outBytes(1,fileskip,posskip,cskip);
						posskip = -1;
                        cskip = 0;
                    }
                    if (cinsert > 0)
                    {
						outBytes(2,fileinsert,posinsert,cinsert);
						posinsert = -1;
                        cinsert = 0;
                    }
                }
            }
            state = newstate;
        }

        switch (state)
        {
            case COPY:
				if (poscopy < 0)
				{
					poscopy = f.tell()-1;
					filecopy = f;
				}
                ccopy += c;
            break;
            case SKIP:
				if (posskip < 0)
				{
					posskip = f.tell();
					fileskip = f;
				}
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

	protected boolean  difFindMatch(boolean flipFiles) throws IOException
    {
		PushbackRandomFile lf2;
    	if (flipFiles)
    	{
    		lf2 = f1;
    	}
    	else
		{
			lf2 = f2;
		}

        long orig = lf2.tell();

        boolean endoffile = false;
        int cs = cMaxSearch;
        while ((cs-- > 0) && !endoffile && !difMatch(flipFiles))
        {
			endoffile = (lf2.read() == EOF);
        }

        boolean found = (!endoffile && (cs > 0));

        lastmark = lf2.tell();

        lf2.seek(orig);

        return found;
    }

    protected boolean difMatch(boolean flipFiles) throws IOException
    {
		PushbackRandomFile lf1;
		PushbackRandomFile lf2;
		if (flipFiles)
		{
			lf1 = f2;
			lf2 = f1;
		}
		else
		{
			lf1 = f1;
			lf2 = f2;
		}

        long orig1 = lf1.tell();
		long orig2 = lf2.tell();

        boolean same = true;
        for (int i = 0; i < cMinMatch && same; ++i)
        {
            int c1 = lf1.read();
            int c2 = lf2.read();
            if ((c2 == EOF && c1 != EOF) || (c1 != c2))
            {
				same = false;
            }
        }

		lf1.seek(orig1);
		lf2.seek(orig2);

        return same;
    }
}
