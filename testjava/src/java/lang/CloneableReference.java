package java.lang;

public class LangTest implements Cloneable
{
    public Object clone()
    {
        Object c = null;
        try
        {
            c = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
        }
        return c;
    }
}
