/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.cmdline;

/**
 * Represents an option (typically specified
 * by the user on the command line).
 * 
 * @author Chris Mosher
 */
public class Option
{
    private OptionDefinition mDef;
    private String mValue;
    private boolean mIsSpecified;

    public Option(OptionDefinition def, String value, boolean isSpecified)
    {
        mDef = def;
        mValue = value;
        mIsSpecified = isSpecified;
    }

    boolean isSpecified()
    {
        return mIsSpecified;
    }
    String getValue()
    {
        return mValue;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof Option))
        {
            return false;
        }
        Option that = (Option)obj;
        return
            this.mDef.equals(that.mDef) &&
            this.mValue.equals(that.mValue) &&
            this.mIsSpecified==that.mIsSpecified;
    }

    public int hashCode()
    {
        int hash = 17;
        hash *= 37;
        hash += mDef.hashCode();
        hash *= 37;
        hash += mValue.hashCode();
        hash *= 37;
        hash += (mIsSpecified ? 0 : 1);
        return hash;
    }
}
