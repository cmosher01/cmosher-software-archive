package com.surveysampling.util;

import java.util.LinkedList;

public class Undoer
{
    private Cloneable mCurrent;
    private final LinkedList mrUndo = new LinkedList();
    private final LinkedList mrRedo = new LinkedList();

//    public void saveForUndo(Cloneable state) throws CloneNotSupportedException
//    {
//        mrUndo.addLast(new ImmutableReference(state));
//        mrRedo.clear();
//    }
//
//    public Cloneable undo(Cloneable state) throws CloneNotSupportedException
//    {
//        mrRedo.addFirst(new ImmutableReference(state));
//        return ((ImmutableReference)mrUndo.removeLast()).object();
//    }
//
//    public Cloneable redo(Cloneable state) throws CloneNotSupportedException
//    {
//        mrUndo.addLast(new ImmutableReference(state));
//        return ((ImmutableReference)mrRedo.removeFirst()).object();
//    }
    public Undoer(Cloneable state)
    {
        mCurrent = state;
    }

    public void saveForUndo() throws CloneNotSupportedException
    {
        mrUndo.addLast(Cloner.cloneObject(mCurrent));
        mrRedo.clear();
    }

    public Cloneable undo() throws CloneNotSupportedException
    {
        mrRedo.addFirst(Cloner.cloneObject(mCurrent));
        return (Cloneable)mrUndo.removeLast();
    }

    public Cloneable redo() throws CloneNotSupportedException
    {
        mrUndo.addLast(Cloner.cloneObject(mCurrent));
        return (Cloneable)mrRedo.removeFirst();
    }
}
