package com.ipc.uda.service.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A simple registry. Maintains a list of registrants,
 * and provides an iterator over the list.
 * Objects of this class are not thread-safe.
 * @param <T> the type of objects registered
 * 
 * @author mosherc
 */
public class Registry<T> implements Iterable<T>
{
    private final Collection<T> taskList = new ArrayList<T>();

    /**
     * Registers the given registrant.
     * @param registrant
     */
    public void register(final T registrant)
    {
        this.taskList.add(registrant);
    }

    /**
     * Unregisters the given registrant. If the given
     * registrant was not previously registered, then
     * this method does nothing.
     * @param registrant
     */
    public void unregister(final T registrant)
    {
        this.taskList.remove(registrant);
    }

    /**
     * Gets a new {@link Iterator} that can be used to
     * iterate through every currently registered object.
     * @return iterator for registrants
     */
    @Override
    public Iterator<T> iterator()
    {
        return this.taskList.iterator();
    }
}
