package com.surveysampling.beans.editors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

public class IntegerEditor extends PropertyEditorSupport implements PropertyEditor
{
    public static final int DEFAULT_PRIMITIVE = 0;
    public static final Integer DEFAULT = new Integer(DEFAULT_PRIMITIVE);

    public String getJavaInitializationString()
    {
        return DEFAULT.toString();
    }

    public void setAsText(String text) throws IllegalArgumentException
    {
        setValue(Integer.valueOf(text));
    }
}
