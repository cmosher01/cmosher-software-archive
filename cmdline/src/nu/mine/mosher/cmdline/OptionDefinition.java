/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.cmdline;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class OptionDefinition implements Comparable
{
    private final String optionLong;
    private final char optionShort;
    private final String desc;
    private final boolean hasValue;

    /**
     * @param optionLong
     * @param optionShort
     * @param desc
     */
    public OptionDefinition(String optionLong, char optionShort, String desc, boolean hasValue)
    {
        if (optionLong == null || desc == null)
        {
            throw new NullPointerException();
        }
        this.optionLong = optionLong;
        this.optionShort = optionShort;
        this.desc = desc;
        this.hasValue = hasValue;

        if (this.optionLong.length() < 2)
        {
            throw new IllegalArgumentException("long option must be 2 or more characters in length");
        }
    }
    public String getDesc()
    {
        return desc;
    }
    public boolean hasValue()
    {
        return hasValue;
    }
    public boolean is(String name)
    {
        if (name.length() == 0)
        {
            throw new IllegalArgumentException("name cannot be empty");
        }
        if (name.length() == 1)
        {
            return optionShort == name.charAt(0);
        }
        return optionLong.equals(name);
    }
    public String getOptionLong()
    {
        return optionLong;
    }
    public char getOptionShort()
    {
        return optionShort;
    }
    public boolean equals(Object obj)
    {
        if (!(obj instanceof OptionDefinition))
        {
            return false;
        }
        OptionDefinition that = (OptionDefinition)obj;
        return
            this.optionLong.equals(that.optionLong) &&
            this.optionShort == that.optionShort &&
            this.hasValue == that.hasValue;
    }
    public int hashCode()
    {
        int hash = 17;
        hash *= 37;
        hash += this.optionLong.hashCode();
        hash *= 37;
        hash += this.optionShort;
        hash *= 37;
        hash += this.hasValue ? 0 : 1;
        return hash;
    }
    public int compareTo(Object obj)
    {
        OptionDefinition that = (OptionDefinition)obj;
        int c = 0;
        if (c == 0)
        {
            c = this.optionLong.compareTo(that.optionLong);
        }
        if (c == 0)
        {
            if (this.optionShort < that.optionShort)
            {
                c = -1;
            }
            else if (this.optionShort > that.optionShort)
            {
                c = +1;
            }
        }
        if (c == 0)
        {
            if (!this.hasValue && that.hasValue)
            {
                c = -1;
            }
            else if (this.hasValue && !that.hasValue)
            {
                c = +1;
            }
        }
        return c;
    }
}
