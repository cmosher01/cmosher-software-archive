package com.surveysampling.util;

import java.util.LinkedList;

public class Undoer
{
    private final LinkedList mrUndo = new LinkedList();
    private final LinkedList mrRedo = new LinkedList();

    public void saveForUndo(Cloneable state) throws CloneNotSupportedException
    {
        mrUndo.addLast(Cloner.cloneObject(state));
        mrRedo.clear();
    }

    public Cloneable undo(Cloneable state) throws CloneNotSupportedException
    {
        mrRedo.addFirst(state);
        return (Cloneable)mrUndo.removeLast();
    }

    public Cloneable redo(Cloneable state) throws CloneNotSupportedException
    {
        mrUndo.addLast(state);
        return (Cloneable)mrRedo.removeFirst();
    }
}
