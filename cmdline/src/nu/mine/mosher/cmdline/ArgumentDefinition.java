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

    /**
     * @param name
     * @param abbrev
     * @param desc
     */
    public ArgumentDefinition(final String name, final String abbrev, final String desc)
    {
        this.name = name;
        this.abbrev = abbrev;
        this.desc = desc;
    }
}
