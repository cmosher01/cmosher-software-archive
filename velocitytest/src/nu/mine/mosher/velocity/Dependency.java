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

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return
     */
    public boolean isSource()
    {
        return source;
    }

    /**
     * @return
     */
    public String getVersion()
    {
        return version;
    }
}
