package nu.mine.mosher.util;

public class MutablePair
{
    private Object a;
	private Object b;
	private boolean swapped;

	public MutablePair(Object first, Object second)
	{
        if (first == null || second == null)
        {
            throw new IllegalArgumentException();
        }
		a = first;
		b = second;
	}

	public Object first()
	{
		return a;
	}

	public Object second()
	{
		return b;
	}

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

	public int hashCode()
	{
        return a.hashCode() ^ b.hashCode();
	}

    public boolean equals(Object o)
    {
        if (!(o instanceof Pair))
        {
            return false;
        }
        MutablePair that = (MutablePair)o;

        return this.a.equals(that.a) && this.b.equals(that.b);
    }

	public void swap()
	{
		swapped = !swapped;
		Object t = a;
		a = b;
		b = t;
	}

	public boolean isSwapped()
	{
		return swapped;
	}
}
