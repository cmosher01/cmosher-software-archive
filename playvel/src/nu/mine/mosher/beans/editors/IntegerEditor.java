/*
 * Created on Apr 14, 2004
 */
package nu.mine.mosher.beans.editors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

/**
 * @author Chris Mosher
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
