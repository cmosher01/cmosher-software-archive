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
    public ArgumentDefinition(String name, String desc)
    {
        if (name == null || desc == null)
        {
            throw new NullPointerException();
        }
        if (name.length() == 0)
        {
            name = "argument";
        }
        this.name = name;
        this.desc = desc;
    }
}
