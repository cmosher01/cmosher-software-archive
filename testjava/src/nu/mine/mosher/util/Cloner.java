package java.lang;

public class Cloner
{
    private Cloner()
    {
        throw new UnsupportedOperationException();
    }

    public static Cloneable cloneObject(Cloneable cloneableObject) throws CloneNotSupportedException
    {
        return (Cloneable)cloneableObject.clone();
    }
}
