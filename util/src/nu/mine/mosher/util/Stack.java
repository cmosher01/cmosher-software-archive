/*
 * Created on Jun 18, 2004
 */
package nu.mine.mosher.util;

/**
 * A LIFO stack.
 */
public interface Stack
{
    /**
     * Pushes the given <code>Object</code> onto
     * the top of this <code>Stack</code>. (Optional operation.)
     * @param obj the <code>Object</code> to be pushed
     * onto this <code>Stack</code>.
     * @throws UnsupportedOperationException if this method is not
     * supported by this <code>Stack</code>.
     * @throws ClassCastException if the class of the specified element
     * prevents it from being added to this <code>Stack</code>.
     * @throws NullPointerException if the specified element is null and this
     * <code>Stack</code> does not support null elements.
     * @throws IllegalArgumentException if some aspect of this element
     * prevents it from being added to this list.
     */
    void push(Object obj);

    /**
     * Removes the <code>Object</code> from the
     * top of this <code>Stack</code>, and returns it.
     * @return the <code>Object</code> at the top of this <code>Stack</code>
     * @throws EmptyStackException if this <code>Stack</code>
     * is currently empty.
     */
    Object pop();

    /**
     * Returns <code>true</code> if and only if this <code>Stack</code>
     * is currently empty.
     * @return <code>true</code> iff empty
     */
    boolean isEmpty();




    /**
     * Returns a reference to the <code>Object</code>
     * currently at the top of this <code>Stack</code>.
     * (Optional operation.)
     * Unlike <code>pop</code>, this method does not
     * remove the <code>Object</code> from this <code>Stack</code>.
     * @return the <code>Object</code> at the top of this <code>Stack</code>
     * @throws EmptyStackException if this <code>Stack</code>
     * is currently empty.
     * @throws UnsupportedOperationException if this method is not
     * supported by this <code>Stack</code>.
     */
    Object peek();

    /**
     * Returns the count of <code>Object</code> currently
     * on this <code>Stack</code>.
     * (Optional operation.)
     * @return size of this <code>Stack</code>
     * @throws UnsupportedOperationException if this method is not
     * supported by this <code>Stack</code>.
     */
    int size();
}
