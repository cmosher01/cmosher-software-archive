package nu.mine.mosher.unicode;

import java.util.Arrays;

public class String32 implements Comparable<String32>
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
		this.rChar32 = rChar32.clone();
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

    @Override
	public String toString()
    {
        return toUTF16();
    }

    public int size()
    {
        return this.rChar32.length;
    }

    public int[] get()
    {
        return this.rChar32.clone();
    }

    public int charAt(int i)
    {
        return this.rChar32[i];
    }

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof String32))
        {
            return false;
        }
		String32 that = (String32)o;
		return Arrays.equals(this.rChar32,that.rChar32);
	}

    @Override
	public int compareTo(String32 that)
    {
		for (int i = 0; i < Math.min(this.rChar32.length,that.rChar32.length); ++i)
		{
            int cmp = String32.compare(this.rChar32[i],that.rChar32[i]);
            if (cmp != 0)
            {
                return cmp;
            }
		}
        return String32.compare(this.rChar32.length,that.rChar32.length);
    }

	private static int compare(final int x0, final int x1)
	{
		return x0 < x1 ? -1 : x1 < x0 ? +1 : 0;
	}

	protected int getHashCode()
	{
		int h = 17;

		for (int i = 0; i < this.rChar32.length; ++i)
        {
			h *= 37;
			h += this.rChar32[i];
        }

		return h;
	}

    @Override
	public int hashCode()
    {
    	return this.hash;
    }
}
