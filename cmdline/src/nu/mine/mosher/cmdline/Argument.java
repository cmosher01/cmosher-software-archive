/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.cmdline;

/**
 * Represents an argument (typically specified
 * by the user on the command line).
 * 
 * @author Chris Mosher
 */
public class Argument
{
    private ArgumentDefinition mDef;
    private String mValue;

    public Argument(ArgumentDefinition def, String value)
    {
        mDef = def;
        mValue = value;
    }

    String getValue()
    {
        return mValue;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof Argument))
        {
            return false;
        }
        Argument that = (Argument)obj;
        return
            this.mDef.equals(that.mDef) &&
            this.mValue.equals(that.mValue);
    }

    public int hashCode()
    {
        int hash = 17;
        hash *= 37;
        hash += mDef.hashCode();
        hash *= 37;
        hash += mValue.hashCode();
        return hash;
    }
}
