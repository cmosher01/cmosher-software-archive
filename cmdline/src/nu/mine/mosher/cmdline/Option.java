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

    public Option(OptionDefinition def, String value)
    {
        mDef = def;
        mValue = value;
    }

    public String getValue()
    {
        return mValue;
    }

    public OptionDefinition getDefinition()
    {
        return mDef;
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

    /**
     * Convenience method.
     * @param name
     * @return
     */
    public boolean is(String name)
    {
        return mDef.is(name);
    }
}
