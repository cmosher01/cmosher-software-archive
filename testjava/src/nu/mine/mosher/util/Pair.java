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
        if (this.a == null)
        {
            return that.a == null;
        }
        else if (!this.a.equals(that.a))
        {
            return false;
        }
        else if (this.b == null)
        {
            return that.b == null;
        }
        else if (!this.b.equals(that.b))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
