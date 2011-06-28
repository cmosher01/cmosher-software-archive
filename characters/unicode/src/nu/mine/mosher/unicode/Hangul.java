package nu.mine.mosher.unicode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Performs Hangul character compositions and decompositions.
 * See Unicode 4.0 standard, section 3.12 "Conjoining Jamo Behavior."
 * 
 * @author Chris Mosher
 */
public class Hangul
{
	private Hangul()
	{
	}

	public static String32 compose(String32 s32)
	{
		int[] rc = s32.get();
		List lc = intArrayToList(rc);

        composeList(lc);

		return new String32(intListToArray(lc));
	}

    public static void composeList(List/*<Integer>*/ listInteger)
    {
        int i = 0;
        while (i < listInteger.size()-1)
        {
            int l = getChar(listInteger,i)-0x1100;
        	int v = getChar(listInteger,i+1)-0x1161;
        	int t = getChar(listInteger,i+2)-0x11a7;
        	if (l < 0 || 19 <= l || v < 0 || 21 <= v)
        	{
        		++i;
        	}
        	else
        	{
        		int j = i+3;
        		if (t <= 0 || 28 <= t)
        		{
        			t = 0;
        			--j;
        		}
        		int s = (l*21+v)*28+t+0xac00;
        		assert s <= 0xffff;
        
        		listInteger.set(i++,new Integer(s));
        		for (int rem = j-1; rem >= i; --rem)
        		{
        			listInteger.remove(rem);
                }
        	}
        }
    }

	public static String32 decompose(String32 s32)
	{
		int[] rc = s32.get();
		List lc = intArrayToList(rc);

        decomposeList(lc);

		return new String32(intListToArray(lc));
	}

	public static void decomposeList(List/*<Integer>*/ listInteger)
    {
        int i = 0;
        while (i < listInteger.size())
        {
        	int s = getChar(listInteger,i)-0xac00;
        	if (0 <= s && s <= 11172)
        	{
        		int l = 0x1100+s/588;
        		int v = 0x1161+(s%588)/28;
        		listInteger.set(i,new Integer(l));
        		listInteger.add(i+1,new Integer(v));
        		int t = 0x11a7+s%28;
        		if (t > 0x11a7)
        		{
        			listInteger.add(i+2,new Integer(t));
        		}
        	}
        	++i;
        }
    }

	private static int getChar(List listInteger, int index)
	{
		if (index >= listInteger.size())
			return -1;

		Integer i = (Integer)listInteger.get(index);
		return i.intValue();
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
}
