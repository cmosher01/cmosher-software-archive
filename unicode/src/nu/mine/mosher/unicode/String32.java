package nu.mine.mosher.unicode;

import java.util.Arrays;

import nu.mine.mosher.util.Immutable;
import nu.mine.mosher.util.Util;

public class String32 implements Comparable, Immutable
{
	private static final String32 EMPTY = new String32();

	private final int[] rChar32;
	private final int hash;

	private String32()
	{
		this(new int[0]);
	}

	public String32(int[] rChar32)
	{
		this.rChar32 = (int[])rChar32.clone();
		this.hash = getHashCode();
	}

	public static String32 getEmpty()
	{
		return EMPTY;
	}

	public static String32 fromUTF16(String s)
	{
		return ConvertUTF.convert16to32(s);
	}

    public String toUTF16()
    {
        return ConvertUTF.convert32to16(this);
    }

    public String toString()
    {
        return toUTF16();
    }

    public int size()
    {
        return rChar32.length;
    }

    public int[] get()
    {
        return (int[])rChar32.clone();
    }

    public int charAt(int i)
    {
        return rChar32[i];
    }

	public boolean equals(Object o)
	{
		if (!(o instanceof String32))
        {
            return false;
        }
		String32 that = (String32)o;
		return Arrays.equals(this.rChar32,that.rChar32);
	}

    public int compareTo(Object o)
    {
		String32 that = (String32)o;
		for (int i = 0; i < Math.min(this.rChar32.length,that.rChar32.length); ++i)
		{
            int cmp = Util.compare(this.rChar32[i],that.rChar32[i]);
            if (cmp != 0)
            {
                return cmp;
            }
		}
        return Util.compare(this.rChar32.length,that.rChar32.length);
    }

	protected int getHashCode()
	{
		int h = 17;

		for (int i = 0; i < rChar32.length; ++i)
        {
			h *= 37;
			h += rChar32[i];
        }

		return h;
	}

    public int hashCode()
    {
    	return hash;
    }
}
