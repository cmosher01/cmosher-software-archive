package nu.mine.mosher.util;

public final class UndoableReference
{
    private Cloneable state;
    private Undoer undoer = new Undoer();

    public UndoableReference(Cloneable state)
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

    public void save() throws CloneNotSupportedException
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

    public boolean canUndo()
    {
        return undoer.canUndo();
    }

    public boolean canRedo()
    {
        return undoer.canRedo();
    }
}
