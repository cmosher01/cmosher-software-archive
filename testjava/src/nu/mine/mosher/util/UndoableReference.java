package com.surveysampling.util;

public class UndoableReference
{
    private Cloneable state;

    public UndoableReference(Cloneable object) throws CloneNotSupportedException
    {
        if (object == null)
        {
            throw new IllegalArgumentException();
        }
        state = object;
    }

}
