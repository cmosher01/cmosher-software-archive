import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

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
    public static void main(String[] args)
    {
    }

    private static final int START = 0;
    private static final int COPY = 1;
    private static final int SKIP = 2;
    private static final int INSERT = 3;
    private static final int END = 4;

    private static final int EOF = -1;

    private final TellStream f1;
    private final TellStream f2;
    private final int cMinMatch;
    private final int cMaxSearch;

	private int state;
	private int ccopy;
	private int cskip;
	private int cinsert;
	private int posinsert = -1;
	private TellStream fileinsert;
	private int lastmark;

    public BinDiff(InputStream f1, InputStream f2, int cMinMatch, int cMaxSearch)
    {
        this.f1 = new TellStream(f1);
        this.f2 = new TellStream(f2);
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

	protected void statechange(int newstate, int c)
	{
		statechange(newstate,c,null);
	}

    protected void statechange(int newstate, int c, TellStream instr)
    {
        if (newstate == start)
        {
            state = start;
            return;
        }

		StringBuffer s = new StringBuffer(256);
        if (newstate != state)
        {
            switch (state) //old state
            {
                case copy :
                    sprintf(s, "c%d", ccopy);
                    fputs(s, fdif);
                    ccopy = 0;
                    break;
                case skip :
                case insert :
                    {
                        if (newstate == copy || newstate == end)
                        {
                            if (cskip)
                            {
                                sprintf(s, "s%d", cskip);
                                fputs(s, fdif);
                                cskip = 0;
                            }
                            if (cinsert)
                            {
								StringBuffer s = new StringBuffer(256);
                                sprintf(s, "i%d{", cinsert);
                                fputs(s, fdif);

                                long orig = ftell(fileinsert);

                                fseek(fileinsert, posinsert, SEEK_SET);
                                for (int i(0); i < cinsert; i++)
                                    fputc(fgetc(fileinsert), fdif);

                                fseek(fileinsert, orig, SEEK_SET);

                                fputc('}', fdif);
                                cinsert = 0;
                                posinsert = -1;
                            }
                        }
                    }
            }
            state = newstate;
        }

        switch (state)
        {
            case copy :
                ccopy += c;
                break;
            case skip :
                cskip += c;
                for (i = 0; i < c; i++)
                     (void)fgetc(f);
                break;
            case insert :
                if (posinsert < 0)
                {
                    posinsert = ftell(f);
                    fileinsert = f;
                }
                cinsert += c;
                for (i = 0; i < c; i++)
                     (void)fgetc(f);
                break;
        }
    }

	protected boolean  difFindMatch() throws IOException
    {
        f2.mark();

        boolean endoffile = false;
        int cs = cMaxSearch;
        while ((cs-- > 0) && !endoffile && !difMatch())
        {
			endoffile = (f2.read() == EOF);
        }

        boolean found = (!endoffile && (cs > 0));

        lastmark = f2.tell();

        f2.reset();

        return found;
    }

    protected boolean difMatch() throws IOException
    {
        f1.mark();
        f2.mark();

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

		f1.reset();
		f2.reset();

        return same;
    }

//    //	returns the fseek of f when it it's at EOF
//    long calceof(FILE * f)
//    {
//        long orig = ftell(f);
//
//        fseek(f, 0, SEEK_END);
//        int poseof = ftell(f);
//
//        fseek(f, orig, SEEK_SET);
//
//        return poseof;
//    }
//
//    void applydif(const char * file_1, const char * file_dif, const char * file_2)
//    {
//        FILE * f1 = fopen(file_1, "rb");
//        FILE * f2 = fopen(file_dif, "rb");
//        FILE * f3 = fopen(file_2, "wb");
//
//        int cdif, i, c;
//        while ((cdif = fgetc(f2)) != EOF)
//        {
//            fscanf(f2, "%d", & c);
//            switch (cdif)
//            {
//                case 'c' :
//                    for (i = 0; i < c; i++)
//                        fputc(fgetc(f1), f3);
//                    break;
//                case 's' :
//                    for (i = 0; i < c; i++)
//                         (void)fgetc(f1);
//                    break;
//                case 'i' :
//                    fgetc(f2); // {
//                    for (i = 0; i < c; i++)
//                        fputc(fgetc(f2), f3);
//                    fgetc(f2); // }
//                    break;
//            }
//        }
//
//        fclose(f1);
//        fclose(f2);
//        fclose(f3);
//    }
}
