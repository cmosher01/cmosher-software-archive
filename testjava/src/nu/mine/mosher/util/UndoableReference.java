package com.surveysampling.util;

public class UndoableReference
{
    private Cloneable state;
    private Undoer undoer;

    public UndoableReference(Cloneable object) throws CloneNotSupportedException
    {
        if (object == null)
        {
            throw new IllegalArgumentException();
        }
        state = object;
    }

    public Cloneable state()
    {
        return state;
    }

    public void saveForUndo() throws CloneNotSupportedException
    {
        undoer.saveForUndo(state);
    }

    public void undo() throws CloneNotSupportedException
    {
        state = undoer.undo(state);
    }

    public void redo() throws CloneNotSupportedException
    {
        state = undoer.redo(state);
    }
}
