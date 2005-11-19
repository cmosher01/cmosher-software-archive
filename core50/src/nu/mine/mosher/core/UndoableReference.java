package nu.mine.mosher.core;

public final class UndoableReference<T extends Cloneable>
{
	private T state;
	private final Undoer<T> undoer = new Undoer<T>();

	public UndoableReference(final T state)
	{
		this.state = state;
		if (this.state == null)
		{
			throw new IllegalArgumentException();
		}
	}

	public T state()
	{
		return this.state;
	}

	public void save() throws CloningException
	{
		this.undoer.save(this.state);
	}

	public void undo()
	{
		this.state = this.undoer.undo(this.state);
	}

	public void redo()
	{
		this.state = this.undoer.redo(this.state);
	}

	public boolean canUndo()
	{
		return this.undoer.canUndo();
	}

	public boolean canRedo()
	{
		return this.undoer.canRedo();
	}
}
