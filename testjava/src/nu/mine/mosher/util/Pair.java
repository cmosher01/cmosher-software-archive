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
}
