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
    private String mName;
    private String mAbbrev;
    private Object mValue;
    private boolean mIsSpecified;
    private String mDesc;

    public Argument(String name, String abbrev, String desc, Object value, boolean isSpecified)
    {
        mName = name;
        mAbbrev = abbrev;
        mDesc = desc;
        mValue = value;
        mIsSpecified = isSpecified;
    }

    boolean isOption()
    {
        return mName.length() > 0 || mAbbrev.length() > 0;
    }
    boolean isSpecified()
    {
        return mIsSpecified;
    }
    String getName()
    {
        if (mName.length() > 0)
        {
            return mName;
        }
        return mAbbrev;
    }
    String getLongName()
    {
        return mName;
    }
    String getShortName()
    {
        return mAbbrev;
    }
    Object getValue()
    {
        return mValue;
    }
    String getDescription()
    {
        return mDesc;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof Argument))
        {
            return false;
        }
        Argument that = (Argument)obj;
        return
            this.mName.equals(that.mName) &&
            this.mAbbrev.equals(that.mAbbrev) &&
            this.mValue.equals(that.mValue) &&
            this.mIsSpecified==that.mIsSpecified &&
            this.mDesc.equals(that.mDesc);
    }

    public int hashCode()
    {
        int hash = 17;
        hash *= 37;
        hash += mName.hashCode();
        hash *= 37;
        hash += mAbbrev.hashCode();
        hash *= 37;
        hash += mValue.hashCode();
        hash *= 37;
        hash += (mIsSpecified ? 0 : 1);
        hash *= 37;
        hash += mDesc.hashCode();
        return hash;
    }
}
