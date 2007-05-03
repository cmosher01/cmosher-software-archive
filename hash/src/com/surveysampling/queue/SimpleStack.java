/*
 * Created on May 9, 2006
 */
package com.surveysampling.queue;

import java.util.LinkedList;

/**
 * A simple stack. Not thread-safe.
 * 
 * @author Chris Mosher
 * @param <E> element type
 */
public class SimpleStack<E>
{
    private final LinkedList<E> stack = new LinkedList<E>();

    /**
     * Puts the given element onto the top of this stack.
     * @param element the element to push
     */
    public void push(final E element)
    {
        if (element == null)
        {
            throw new IllegalArgumentException("Cannot add null element to stack.");
        }
        this.stack.addFirst(element);
    }

    /**
     * Removes and returns the element at the top of this stack,
     * or returns null if this stack is empty.
     * @return the top of this stack, or null
     */
    public E pop()
    {
        return this.stack.poll();
    }

    @Override
    public String toString()
    {
        if (this.stack.isEmpty())
        {
            return "[empty stack]";
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("<");
        for (final E element : this.stack)
        {
            sb.append(element);
            sb.append("|");
        }

        return sb.toString();
    }
}
