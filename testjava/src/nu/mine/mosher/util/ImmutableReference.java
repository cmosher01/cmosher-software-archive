import java.lang.reflect.InvocationTargetException;

import mosher.Cloner;

public class ImmutableReference
{
    private Cloneable ref;

    public ImmutableReference(Cloneable ref) throws IllegalArgumentException, SecurityException, CloneNotSupportedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        if (ref == null)
        {
            throw new NullPointerException();
        }
        this.ref = Cloner.cloneObject(ref);
    }

    public Cloneable object() throws IllegalArgumentException, SecurityException, CloneNotSupportedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return Cloner.cloneObject(this.ref);
    }

    public Object clone() throws CloneNotSupportedException
    {
        ImmutableReference clon = null;
        try
        {
            clon = (ImmutableReference)super.clone();
            clon.ref = object();
        }
        catch (Throwable ex)
        {
            CloneNotSupportedException ex2 = new CloneNotSupportedException();
            ex2.initCause(ex);
            throw ex2;
        }
        return clon;
    }

    public String toString()
    {
        return ref.toString();
    }

    public boolean equals(Object o)
    {
        return this.ref.equals(o);
    }

    public int hashCode()
    {
        return this.ref.hashCode();
    }
}
