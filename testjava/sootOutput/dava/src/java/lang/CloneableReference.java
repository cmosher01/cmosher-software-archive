package java.lang;

public class CloneableReference implements java.lang.Cloneable
{
    private java.lang.Object referent;

    public CloneableReference(java.lang.Object referent)
    {
        super();

        referent = referent;
        return;
    }

    public java.lang.Object clone()
    {
        java.lang.Object c;

        c = null;

        label_0:
        {
            try
            {
                c = referent.clone();
            }
            catch (CloneNotSupportedException e)
            {
                break label_0;
            }
        }

        return c;
    }
}
