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
}
