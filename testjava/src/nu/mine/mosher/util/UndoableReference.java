package com.surveysampling.util;

public class UndoableReference
{
    private Cloneable state;
    private Undoer undoer;

    public UndoableReference(Cloneable state) throws CloneNotSupportedException
    {
        if (state == null)
        {
            throw new IllegalArgumentException();
        }
        this.state = state;
    }

    public Cloneable state()
    {
        return state;
    }

    public void saveForUndo() throws CloneNotSupportedException
    {
        undoer.save(state);
    }

    public void undo()
    {
        state = undoer.undo(state);
    }

    public void redo()
    {
        state = undoer.redo(state);
    }
}
