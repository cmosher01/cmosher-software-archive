public class Pair
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

    public boolean equals(Object obj)
    {
        if (!(obj instanceof Pair))
            return false;
        Pair that = (Pair)obj;

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
}
