package java.lang;

public class Cloner
{
    private Cloner()
    {
        throw new UnsupportedOperationException();
    }

    public Cloneable cloneObject(Cloneable cloneableObject) throws CloneNotSupportedException
    {
        return (Cloneable)cloneableObject.clone();
    }
}
