package java.lang;

public class CloneableReference implements Cloneable
{
    private Object referent;
    public CloneableReference(Object referent)
    {
        this.referent = referent;
    }
    public Object clone()
    {
        Object c = null;
        try
        {
            c = referent.clone();
        }
        catch (CloneNotSupportedException e)
        {
        }
        return c;
    }
}
