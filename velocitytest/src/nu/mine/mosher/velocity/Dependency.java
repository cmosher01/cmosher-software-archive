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

        boolean eq =
            this.name.equalsIgnoreCase(that.name) &&
            this.version.equalsIgnoreCase(that.version) &&
            this.source == that.source;

        return eq;
    }
}
