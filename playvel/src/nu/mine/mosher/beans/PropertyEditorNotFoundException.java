package nu.mine.mosher.beans;

/**
 * @author Chris Moshesr
 */
public class PropertyEditorNotFoundException extends Exception
{
	private final Class forClass;

    public PropertyEditorNotFoundException(Class forClass)
    {
    	this(forClass,null);
    }

    public PropertyEditorNotFoundException(Class forClass, Throwable cause)
    {
		super("Could not get PropertyEditor for class "+forClass.getName(),cause);
		this.forClass = forClass;
    }
}
