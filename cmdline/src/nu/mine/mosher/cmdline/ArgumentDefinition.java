/*
 * Created on Jul 15, 2004
 */
package nu.mine.mosher.cmdline;

/**
 * TODO
 * 
 * @author chrism
 */
public class ArgumentDefinition implements Comparable
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
    public String getDesc()
    {
        return desc;
    }
    public String getName()
    {
        return name;
    }
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ArgumentDefinition))
        {
            return false;
        }
        ArgumentDefinition that = (ArgumentDefinition)obj;
        return this.name.equals(that.name);
    }
    public int hashCode()
    {
        return this.name.hashCode();
    }
    public String toString()
    {
        return this.name;
    }
    public int compareTo(Object o)
    {
        ArgumentDefinition that = (ArgumentDefinition)obj;
        return this.name.compareTo(that.name);
    }
}
