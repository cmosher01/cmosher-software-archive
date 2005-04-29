package nu.mine.mosher.core;

import java.util.LinkedList;

public final class Undoer<T extends Cloneable>
{
	private final LinkedList<T> mrUndo = new LinkedList<T>();
	private final LinkedList<T> mrRedo = new LinkedList<T>();

	public void save(T state) throws CloneNotSupportedException
	{
		mrUndo.addLast(CloneFactory.cloneObject(state));
		mrRedo.clear();
	}

	public T undo(T state)
	{
		mrRedo.addFirst(state);
		return mrUndo.removeLast();
	}

	public T redo(T state)
	{
		mrUndo.addLast(state);
		return mrRedo.removeFirst();
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
