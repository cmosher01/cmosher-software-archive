import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

/*
 * TODO
 *
 * Created on Apr 14, 2004
 */

/**
 * TODO
 */
public class IntegerEditor extends PropertyEditorSupport implements PropertyEditor
{
    public String getJavaInitializationString()
    {
        return "0";
    }

    public void setAsText(String text) throws IllegalArgumentException
    {
        setValue(Integer.valueOf(text));
    }

}
