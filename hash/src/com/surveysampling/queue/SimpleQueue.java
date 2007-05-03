/*
 * Created on May 9, 2006
 */
package com.surveysampling.queue;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A simple queue. Not thread-safe.
 * 
 * @author Chris Mosher
 * @param <E> element type
 */
public class SimpleQueue<E>
{
    private final Queue<E> queue = new LinkedList<E>();

    /**
     * Inserts the given element at the tail of this queue
     * @param element
     */
    public void offer(final E element)
    {
        if (element == null)
        {
            throw new IllegalArgumentException("Cannot add null element to queue.");
        }
        this.queue.offer(element);
    }

    /**
     * Removes and returns the element at the head of this queue,
     * or returns null if this queue is empty.
     * @return the head of this queue, or null
     */
    public E poll()
    {
        return this.queue.poll();
    }

    @Override
    public String toString()
    {
        if (this.queue.isEmpty())
        {
            return "[empty queue]";
        }

        final StringBuilder sb = new StringBuilder();

        for (final E element : this.queue)
        {
            sb.append("<");
            sb.append(element);
        }
        sb.append("<");

        return sb.toString();
    }
}
