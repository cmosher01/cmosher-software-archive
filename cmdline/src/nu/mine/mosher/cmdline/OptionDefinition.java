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
    private final char abbrev;
    private final String desc;
    private final boolean hasValue;

    /**
     * @param optionLong
     * @param abbrev
     * @param desc
     */
    public OptionDefinition(final String name, final char abbrev, final String desc, boolean hasValue)
    {
        this.optionLong = name;
        this.abbrev = abbrev;
        this.desc = desc;
        this.hasValue = hasValue;

        if (this.optionLong.length() < 1)
        {
            throw new IllegalArgumentException("long option must be 2 or more characters in length");
        }
    }
}
