package nu.mine.mosher.core;

public final class UndoableReference<T extends Cloneable>
{
	private T state;
	private final Undoer<T> undoer = new Undoer<T>();

	public UndoableReference(T state)
	{
		this.state = state;
		if (this.state == null)
		{
			throw new IllegalArgumentException();
		}
	}

	public T state()
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
