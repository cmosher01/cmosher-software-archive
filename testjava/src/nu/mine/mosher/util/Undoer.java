package com.surveysampling.util;

import java.util.LinkedList;

public class Undoer
{
    private final LinkedList mrUndo = new LinkedList();
    private final LinkedList mrRedo = new LinkedList();

    public void saveForUndo(Cloneable state) throws CloneNotSupportedException
    {
        mrUndo.addLast(new ImmutableReference(state));
        mrRedo.clear();
    }

    public Cloneable undo(Cloneable state) throws CloneNotSupportedException
    {
        mrRedo.addFirst(new ImmutableReference(state));
        ImmutableReference prevState = (ImmutableReference)mrUndo.removeLast();
        return prevState.object();
    }

    public Cloneable redo(Cloneable state) throws CloneNotSupportedException
    {
        mrUndo.addLast(new ImmutableReference(state));
        ImmutableReference prevState = (ImmutableReference)mrUndo.removeFirst();
        return prevState.object();
    }
}
