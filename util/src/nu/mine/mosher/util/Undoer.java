package nu.mine.mosher.util;

import java.util.LinkedList;

public final class Undoer
{
    private final LinkedList mrUndo = new LinkedList();
    private final LinkedList mrRedo = new LinkedList();

    public void save(Cloneable state) throws CloneNotSupportedException
    {
        mrUndo.addLast(Cloner.cloneObject(state));
        mrRedo.clear();
    }

    public Cloneable undo(Cloneable state)
    {
        mrRedo.addFirst(state);
        return (Cloneable)mrUndo.removeLast();
    }

    public Cloneable redo(Cloneable state)
    {
        mrUndo.addLast(state);
        return (Cloneable)mrRedo.removeFirst();
    }

    public boolean canUndo()
    {
        return !mrUndo.isEmpty();
    }

    public boolean canRedo()
    {
        return !mrRedo.isEmpty();
    }
}
