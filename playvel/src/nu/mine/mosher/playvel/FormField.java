/*
 * Created on Apr 13, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.playvel;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FormField
{
	private final String name;
	private final Object value;
	private final Throwable exception;

	public FormField(String name, Object value)
	{
		this(name,value,null);
	}

	public FormField(String name, Object value, Throwable exception)
	{
		this.name = name;
		this.value = value;
		this.exception = exception;
	}

	public boolean isBad()
	{
		return exception != null;
	}

    public Throwable getException()
    {
        return exception;
    }

    public String getName()
    {
        return name;
    }

    public Object getValue()
    {
        return value;
    }
}
