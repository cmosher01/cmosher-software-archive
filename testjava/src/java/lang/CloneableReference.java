package java.lang;

public class LangTest implements Cloneable
{
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException e)
        {
        }
    }
}
