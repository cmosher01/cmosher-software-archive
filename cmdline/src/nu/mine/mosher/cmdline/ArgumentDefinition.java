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
        return this.name.equalsIgnoreCase(that.name);
    }
    public int hashCode()
    {
        // TODO Auto-generated method stub
        return super.hashCode();
    }
    public String toString()
    {
        // TODO Auto-generated method stub
        return super.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        // TODO Auto-generated method stub
        return 0;
    }
}
