/*
 * Created on Jul 15, 2004
 */
package nu.mine.mosher.cmdline;

/**
 * TODO
 * 
 * @author chrism
 */
public class ArgumentDefinition
{
    private final String name;
    private final String abbrev;
    private final String desc;
    private final boolean hasValue;

    public class SimpleArg extends ArgumentDefinition
    {
        public SimpleArg(String desc)
        {
            super("","",desc,true);
        }
    }

    /**
     * @param name
     * @param abbrev
     * @param desc
     */
    public ArgumentDefinition(final String name, final String abbrev, final String desc, boolean hasValue)
    {
        this.name = name;
        this.abbrev = abbrev;
        this.desc = desc;
        this.hasValue = hasValue;
    }
}
