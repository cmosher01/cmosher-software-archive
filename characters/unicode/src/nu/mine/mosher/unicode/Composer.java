package nu.mine.mosher.unicode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;



public class Composer
{
    private final CompositionExclusions exclude;
	private final UnicodeData unidata;
	private Map compose = new HashMap();
	private Map decomposeCanon = new HashMap();
	private Map decomposeCompat = new HashMap();
	private Map combiningClass = new HashMap();

	public Composer() throws IOException
	{
		this(fileRes("UnicodeData.txt"));
	}

	public Composer(File txtUnicodeData) throws IOException
	{
		this(txtUnicodeData,fileRes("CompositionExclusions.txt"));
	}

    private static File fileRes(String name)
    {
        name = "nu/mine/mosher/unicode/"+name;
        return new File(Composer.class.getClassLoader().getResource(name).getFile());
    }

	public Composer(File txtUnicodeData, File txtCompositionExclusions) throws IOException
	{
		exclude = new CompositionExclusions(txtCompositionExclusions);
		unidata = new UnicodeData(txtUnicodeData,this);
	}

	public void readFromFiles() throws IOException
	{
		exclude.readFromFile();
		unidata.readFromFile();
	}

    public void addCharacter(int nCodePoint, int nCombining, boolean compat, String32 decompSeq)
    {
		Integer codePoint = new Integer(nCodePoint);

		if (nCombining > 0)
		{
			combiningClass.put(codePoint,new Integer(nCombining));
		}

		if (decompSeq.size() > 0)
		{
			decomposeCompat.put(codePoint,decompSeq);
			if (!compat)
			{
				decomposeCanon.put(codePoint,decompSeq);

				if (!exclude.is(nCodePoint) && decompSeq.size() >= 2)
				{
					compose.put(decompSeq,codePoint);
				}
			}
		}
    }

	public String compose(String s)
	{
		return compose(s,false);
	}

	public String compose(String s, boolean compatible)
	{
		return dcc(s,compatible,true);
	}

	public String decompose(String s, boolean compatible)
	{
		return dcc(s,compatible,false);
	}

	public String32 compose(String32 s)
	{
		return compose(s,false);
	}

	public String32 compose(String32 s, boolean compatible)
	{
		return dcc32(s,compatible,true);
	}

	public String32 decompose(String32 s, boolean compatible)
	{
		return dcc32(s,compatible,false);
	}

    private String dcc(String s, boolean compatible, boolean recompose)
    {
		String32 s32 = String32.fromUTF16(s);
		String32 s32Result = dcc32(s32,compatible,recompose);
		return s32Result.toUTF16();
    }

	private String32 dcc32(String32 s32, boolean compatible, boolean recompose)
	{
		List listc32 = intArrayToList(s32.get());

		decomposeOnly(listc32,compatible);
		if (recompose)
			composeOnly(listc32,false);

		Hangul.decomposeList(listc32);
		if (recompose)
			Hangul.composeList(listc32);

		return new String32(intListToArray(listc32));
	}

	private static ArrayList intArrayToList(int[] rc)
	{
		ArrayList list = new ArrayList(rc.length);
		for (int i = 0; i < rc.length; ++i)
		{
			list.add(new Integer(rc[i]));
		}
		return list;
	}

	private static int[] intListToArray(List list)
	{
		int[] r = new int[list.size()];
		int c = 0;
		for (Iterator i = list.iterator(); i.hasNext();)
		{
			Integer x = (Integer)i.next();
			r[c++] = x.intValue();
		}
		return r;
	}

	protected void decomposeOnly(List rc, boolean compatible)
	{
		Map decomp;
		if (compatible)
		{
			decomp = decomposeCompat;
		}
		else
		{
			decomp = decomposeCanon;
		}

		boolean hadDecomps = true;
		while (hadDecomps)
		{
			hadDecomps = false;
			ListIterator lsi = rc.listIterator();
			while (lsi.hasNext())
			{
				Integer csource = (Integer)lsi.next();
				String32 deseq = (String32)decomp.get(csource);
				if (deseq != null)
				{
					hadDecomps = true;

					lsi.remove();
					int[] rseq = deseq.get();
					for (int j = 0; j < rseq.length; ++j)
                    {
						lsi.add(new Integer(rseq[j]));
                    }
				}
			}
		}
		int is = -1;
		List compseqs = new ArrayList();
		int ind = 0;
		for (Iterator i = rc.iterator(); i.hasNext();)
        {
            Integer c = (Integer)i.next();
			Integer combClass = (Integer)combiningClass.get(c);
			int cc = 0;
			if (combClass != null)
			{
				cc = combClass.intValue();
			}
			if (is >= 0)
			{
				if (cc == 0)
				{
					compseqs.add(new Range(is,ind));
					is = -1;
				}
			}
			else if (cc > 0)
			{
				is = ind;
			}
			++ind;
        }
        if (is >= 0)
        {
			compseqs.add(new Range(is,ind));
			is = -1;
        }

		for (int i = 0; i < compseqs.size(); ++i)
		{
            Range se = (Range)compseqs.get(i);
			List combseq = rc.subList(se.start,se.end);
			Collections.sort(combseq,new Comparator()
			{
				private Map localCombClass = getCombiningClass();
				public int compare(Object n1, Object n2)
				{
					Integer c1 = (Integer)n1;
					Integer c2 = (Integer)n2;
					Integer combClass1 = (Integer)localCombClass.get(c1);
					if (combClass1 == null)
						combClass1 = new Integer(0);
					Integer combClass2 = (Integer)localCombClass.get(c2);
					if (combClass2 == null)
						combClass2 = new Integer(0);
					return combClass1.intValue()-combClass2.intValue();
				}
			});
		}
	}

    private static class Range
    {
        public final int start;
        public final int end;
        public Range(int start, int end)
        {
            this.start = start;
            this.end = end;
        }
    }

    protected Map getCombiningClass()
    {
        return combiningClass;
    }

	protected void composeOnly(List rcorig, boolean compatible)
	{
		if (compatible)
			throw new IllegalArgumentException("Compatible composition is not supported (only canonical composition is).");

		int starterPos = 0;
		int compPos = 1;
		Integer starterCh = null;
		int lastClass = 0;

		int[] twoChars = new int[2];

		List rc = new ArrayList(rcorig);

		// Loop on the decomposed characters, combining where possible
		for (int i = 0; i < rc.size(); ++i)
        {
            Integer ch = (Integer)rc.get(i);
            if (starterCh == null) // if at beginning of string
            {
				starterCh = ch;
				Integer cc = (Integer)combiningClass.get(starterCh);
				if (cc != null)
				{
					lastClass = cc.intValue();
				}
				else
				{
					lastClass = 0;
				}

				if (lastClass != 0)
					lastClass = 256; // ??? fix for irregular combining sequence
            }
            else
            {
				int chClass;
				Integer cc = (Integer)combiningClass.get(ch);
				if (cc != null)
				{
					chClass = cc.intValue();
				}
				else
				{
					chClass = 0;
				}

				twoChars[0] = starterCh.intValue();
				twoChars[1] = ch.intValue();
				String32 lookup = new String32(twoChars);
				Integer composite = (Integer)compose.get(lookup);

				if (composite != null && (lastClass < chClass || lastClass == 0))
				{
					rc.set(starterPos,composite);
					starterCh = composite;
				}
				else
				{
					if (chClass == 0)
					{
						starterPos = compPos;
						starterCh = ch;
					}
					lastClass = chClass;
					rc.set(compPos++,ch);
				}
            }
        }
        rc.subList(compPos,rc.size()).clear();
        rcorig.clear();
        rcorig.addAll(rc);
	}

    public void clear()
    {
		compose.clear();
		decomposeCanon.clear();
		decomposeCompat.clear();
		combiningClass.clear();
    }
}
