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
    private final String desc;

    /**
     * @param name
     * @param desc
     */
    public ArgumentDefinition(final String name, final String desc)
    {
        this.name = name;
        this.desc = desc;
        if (this.name == null || this.desc == null)
        {
            throw new NullPointerException();
        }
    }
}
