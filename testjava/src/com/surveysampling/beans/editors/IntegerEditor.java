package com.surveysampling.beans.editors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

public class IntegerEditor extends PropertyEditorSupport implements PropertyEditor
{
    public String getJavaInitializationString()
    {
        return "33";
    }

    public void setAsText(String text) throws IllegalArgumentException
    {
        setValue(Integer.valueOf(text));
    }
}
