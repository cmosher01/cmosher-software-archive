public class ImmutableReference
{
    private Cloneable ref;

    public ImmutableReference(Cloneable ref) throws CloneNotSupportedException
    {
        if (ref == null)
        {
            throw new NullPointerException();
        }
        this.ref = Cloner.cloneObject(ref);
    }

    public Cloneable object() throws CloneNotSupportedException
    {
        return Cloner.cloneObject(this.ref);
    }

    public String toString()
    {
        return ref.toString();
    }

    public Object clone()
    {
        ImmutableReference clon = null;
        try
        {
            clon = (ImmutableReference)super.clone();
            clon.ref = object();
        }
        catch (CloneNotSupportedException cantHappen)
        {
        }
        return clon;
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
