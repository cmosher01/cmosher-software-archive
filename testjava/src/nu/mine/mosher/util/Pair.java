public class Pair implements Cloneable, Comparable
{
    private Object a;
    private Object b;

    public Pair(Object a, Object b)
    {
        this.a = new CloneableReference(a).clone();
        this.b = new CloneableReference(b).clone();
    }

    public Object a()
    {
        return new CloneableReference(a).clone();
    }

    public Object b()
    {
        return new CloneableReference(b).clone();
    }

    public String toString()
    {
        return "("+a+","+b+")";
    }

    public Object clone()
    {
        Pair clon = null;
        try
        {
            clon = (Pair)super.clone();
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

        return eq(this.a,that.a) && eq(this.b,that.b);
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
        c = cmp(this.a,that.a);
        if (c == 0)
        {
            c = cmp(this.b,that.b);
        }

        return c;
    }

    private int cmp(Object x, Object y)
    {
    }

    public int hashCode()
    {
        int h = 17;

        h *= 37;
        h += a==null ? 0 : a.hashCode();

        h *= 37;
        h += b==null ? 0 : b.hashCode();

        return h;
    }
}
