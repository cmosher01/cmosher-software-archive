package mosher; // needs special class loader at runtime

/**
 * Provides a static method that will clone any Cloneable Object.
 * This class needs to be in the java.lang package in order to be
 * able to access the protected clone method of Object.
 */
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
