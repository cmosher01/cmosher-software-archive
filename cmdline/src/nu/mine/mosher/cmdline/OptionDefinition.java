/*
 * Created on Jul 15, 2004
 */
package nu.mine.mosher.cmdline;

/**
 * TODO
 * 
 * @author chrism
 */
public class OptionDefinition
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
        new OptionDefinition(null,null,null,true);
        return desc;
    }
    public boolean hasValue()
    {
        return hasValue;
    }
    public String getOptionLong()
    {
        return optionLong;
    }
    public char getOptionShort()
    {
        return optionShort;
    }
}
