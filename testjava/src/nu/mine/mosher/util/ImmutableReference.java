public class ImmutableReference
{
    private Cloneable ref;

    public ImmutableReference(Cloneable a, Cloneable b) throws CloneNotSupportedException
    {
        this.ref = Cloner.cloneObject(a);
        this.b = Cloner.cloneObject(b);
    }

    public Cloneable a() throws CloneNotSupportedException
    {
        return Cloner.cloneObject(this.ref);
    }

    public Cloneable b() throws CloneNotSupportedException
    {
        return Cloner.cloneObject(this.b);
    }

    public String toString()
    {
        return "("+ref+","+b+")";
    }

    public Object clone()
    {
        Pair clon = null;
        try
        {
            clon = (Pair)super.clone();
            clon.a = a();
            clon.b = b();
        }
        catch (CloneNotSupportedException cantHappen)
        {
        }
        return clon;
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Pair))
        {
            return false;
        }
        Pair that = (Pair)o;

        return eq(this.ref,that.a) && eq(this.b,that.b);
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
        Pair that = (Pair)o;

        int c;
        c = cmp(this.ref,that.a);
        if (c == 0)
        {
            c = cmp(this.b,that.b);
        }

        return c;
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
        int h = 17;

        h *= 37;
        h += ref==null ? 0 : ref.hashCode();

        h *= 37;
        h += b==null ? 0 : b.hashCode();

        return h;
    }
}
