/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.cmdline;

/**
 * Represents an argument (possibly) specified
 * by the user on the command line.
 * 
 * @author Chris Mosher
 */
public class Argument
{
    private final boolean mIsOption;
    private String mName;
    private String mAbbrev;
    private Object mValue;
    private boolean mIsSpecified;
    private String mDesc;

    public Argument(String name, String abbrev, String desc, Object value, boolean isSpecified)
    {
        mIsOption = name.length() > 0 || abbrev.length() > 0;
        mName = name;
        mAbbrev = abbrev;
        mDesc = desc;
        mValue = value;
        mIsSpecified = isSpecified;
    }

    boolean isOption()
    {
        return mIsOption;
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
}
