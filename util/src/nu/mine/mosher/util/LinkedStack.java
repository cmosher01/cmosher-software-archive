/*
 * Created on Jun 12, 2004
 */
package nu.mine.mosher.util;

import java.util.EmptyStackException;
import java.util.LinkedList;

/**
 * An implementation of the <code>Stack</code> interface
 * that uses a <code>LinkedList</code> to hold the
 * current state of the <code>Stack</code>.
 * 
 * @author Chris Mosher
 */
public class LinkedStack implements Stack
{
	LinkedList list = new LinkedList();



	public void push(Object obj)
	{
		list.addFirst(obj);
	}

	public Object pop()
	{
        if (isEmpty())
        {
            throw new EmptyStackException();
        }
		return list.removeFirst();
	}

	public Object peek()
	{
        if (isEmpty())
        {
            throw new EmptyStackException();
        }
		return list.getFirst();
	}

	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	public int size()
	{
		return list.size();
	}
}
