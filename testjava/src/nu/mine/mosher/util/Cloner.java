package java.lang;

public class Cloner
{
    private Cloner()
    {
        throw new UnsupportedOperationException();
    }

    public Cloneable cloneObject(Cloneable object) throws CloneNotSupportedException
    {
        return (Cloneable)object.clone();
    }
}
