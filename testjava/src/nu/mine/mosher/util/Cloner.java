//package java.lang; // needs special class loader at runtime
package mosher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        try
        {
            Method objClone = cloneableObject.getClass().getMethod("clone", null);
            objClone.setAccessible(true);
            return (Cloneable)objClone.invoke(cloneableObject,null);
        }
        catch (Throwable ex)
        {
            CloneNotSupportedException ex2 = new CloneNotSupportedException();
            ex2.initCause(ex);
            throw ex2;
        }
//        return (Cloneable)cloneableObject.clone();
    }
}
