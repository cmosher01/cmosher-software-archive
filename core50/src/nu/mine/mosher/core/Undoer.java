package nu.mine.mosher.core;

import java.util.LinkedList;

public final class Undoer<T extends Cloneable>
{
	private final LinkedList<T> rUndo = new LinkedList<T>();
	private final LinkedList<T> rRedo = new LinkedList<T>();



	public void save(final T state) throws CloningException
	{
		final CloneFactory<T> cf = new CloneFactory<T>(state);
		this.rUndo.addLast(cf.createClone());
		this.rRedo.clear();
	}

	public T undo(final T state)
	{
		this.rRedo.addFirst(state);
		return this.rUndo.removeLast();
	}

	public T redo(final T state)
	{
		this.rUndo.addLast(state);
		return this.rRedo.removeFirst();
	}

	public boolean canUndo()
	{
		return !this.rUndo.isEmpty();
	}

	public boolean canRedo()
	{
		return !this.rRedo.isEmpty();
	}
}
