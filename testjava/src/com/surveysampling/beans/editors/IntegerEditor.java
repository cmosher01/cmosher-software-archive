package com.surveysampling.beans.editors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

public class IntegerEditor extends PropertyEditorSupport implements PropertyEditor
{
    public static final int DEFAULT_INT = 0;
    public String getJavaInitializationString()
    {
        return Integer.toString(DEFAULT_INT);
    }

    public void setAsText(String text) throws IllegalArgumentException
    {
        setValue(Integer.valueOf(text));
    }
}
