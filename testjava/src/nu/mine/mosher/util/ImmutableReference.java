public class ImmutableReference
{
    private Cloneable ref;

    public ImmutableReference(Cloneable ref) throws CloneNotSupportedException
    {
        this.ref = Cloner.cloneObject(ref);
    }

    public Cloneable object() throws CloneNotSupportedException
    {
        return Cloner.cloneObject(this.ref);
    }

    public String toString()
    {
        return ref.toString();
    }

    public Object clone()
    {
        ImmutableReference clon = null;
        try
        {
            clon = (ImmutableReference)super.clone();
            clon.ref = object();
        }
        catch (CloneNotSupportedException cantHappen)
        {
        }
        return clon;
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof ImmutableReference))
        {
            return false;
        }
        ImmutableReference that = (ImmutableReference)o;

        return eq(this.ref,that.ref);
    }

    private boolean eq(Object x, Object y)
    {
        if (x == null && y == null)
        {
            return true;
        }
        if (x == null)
        {
            return false;
        }
        if (y == null)
        {
            return false;
        }
        return x.equals(y);
    }

    public int compareTo(Object o)
    {
        ImmutableReference that = (ImmutableReference)o;
        return cmp(this.ref,that.ref);
    }

    private int cmp(Object x, Object y)
    {
        if (x == null && y == null)
        {
            return 0;
        }
        if (x == null)
        {
            return -1;
        }
        if (y == null)
        {
            return +1;
        }
        return ((Comparable)x).compareTo(y);
    }

    public int hashCode()
    {
        return this.ref.hashCode();
    }
}
