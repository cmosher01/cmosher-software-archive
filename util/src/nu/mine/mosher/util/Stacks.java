/*
 * Created on Jun 18, 2004
 */
package nu.mine.mosher.util;

/**
 * Provides static methods related to the <code>Stack</code> interface.
 * 
 * @author Chris Mosher
 */
public class Stacks
{
    /**
     * Returns an unmodifiable view of the specified <code>Stack</code>.
     * This method allows
     * modules to provide users with "read-only" access to internal <code>Stack</code>s.
     * Query operations on the returned set "read through" to the specified
     * <code>Stack</code>, and attempts to modify the returned <code>Stack</code>,
     * result in an <code>UnsupportedOperationException</code>.
     * @param  stack the <code>Stack</code> for which an unmodifiable view is to be returned.
     * @return an unmodifiable view of thegiven <code>Stack</code>.
     */

    public static Stack unmodifiableStack(Stack stack)
    {
        return new UnmodifiableStack(stack);
    }

    protected static class UnmodifiableStack implements Stack
    {
        private Stack stack;

        protected UnmodifiableStack(Stack stack)
        {
            this.stack = stack;
        }

        public void push(Object obj)
        {
            throw new UnsupportedOperationException();
        }

        public Object pop()
        {
            throw new UnsupportedOperationException();
        }

        public Object peek()
        {
            return stack.peek();
        }

        public boolean isEmpty()
        {
            return stack.isEmpty();
        }

        public int size()
        {
            return stack.size();
        }
    }
}
