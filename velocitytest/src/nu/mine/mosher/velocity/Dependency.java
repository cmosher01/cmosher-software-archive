public class Dependency
{
    private final String name;
    private final String version;
    private final boolean source;

    public Dependency(String name, String version, boolean source)
    {
        this.name = name;
        this.version = version;
        this.source = source;
    }

    public String getName()
    {
        return name;
    }

    public boolean isSource()
    {
        return source;
    }

    public String getVersion()
    {
        return version;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof Dependency))
        {
            return false;
        }
        Dependency that = (Dependency)obj;

        return
            this.name.equalsIgnoreCase(that.name) &&
            this.version.equalsIgnoreCase(that.version) &&
            this.source == that.source;
    }

    public int hashCode()
    {
        int h = 17;

        h *= 37;
        h += name.hashCode();

        h *= 37;
        h += version.hashCode();

        h *= 37;
        h += source ? 0 : 1;

        return h;
    }
}
