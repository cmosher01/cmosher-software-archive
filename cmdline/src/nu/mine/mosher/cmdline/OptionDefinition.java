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
    private final String name;
    private final String abbrev;
    private final String desc;
    private final boolean hasValue;

    /**
     * @param name
     * @param abbrev
     * @param desc
     */
    public OptionDefinition(final String name, final String abbrev, final String desc, boolean hasValue)
    {
        this.name = name;
        this.abbrev = abbrev;
        this.desc = desc;
        this.hasValue = hasValue;

        if (this.name.length() < 1)
        {
            throw new IllegalArgumentException("long option must be 2 or more characters in length");
        }
    }
}
