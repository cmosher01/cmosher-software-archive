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
    public OptionDefinition(final String optionLong, final char optionShort, final String desc, boolean hasValue)
    {
        this.optionLong = optionLong;
        this.optionShort = optionShort;
        this.desc = desc;
        this.hasValue = hasValue;

        if (this.optionLong.length() < 2)
        {
            throw new IllegalArgumentException("long option must be 2 or more characters in length");
        }
    }
}
