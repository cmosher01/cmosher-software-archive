package com.surveysampling.mosher.rwlock;

import java.util.Vector;
import java.util.Enumeration;

/**
 * Handles locking for read/write access to a shared resource.
 * Call lockForReading to wait for read access to the lock before
 * attempting to read from the shared resource. Or call lockForWriting
 * to wait for write access to the lock before attempting to write to
 * the shared resource. Call unlock to release the lock when done
 * accessing the shared resource.
 * Copied and improved from "Java Threads", 2nd ed., by Scott Oaks and
 * Henry Wong (O'Reilly: 1999), pp. 216-218.
 * 
 * @author Chris Mosher
 */
public class RWLock
{
    public RWLock()
    {
    }

    /**
     * Waits (if necessary) and obtains read access to this lock.
     */
    public void lockForReading()
    {
        lock(RWNode.READER);
    }

    /**
     * Waits (if necessary) and obtains write access to this lock.
     */

    public void lockForWriting()
    {
        lock(RWNode.WRITER);
    }

    /**
     * Relinquishes access to this lock.
     */

    public synchronized void unlock()
    {
        Thread me = Thread.currentThread();
        int i = getIndex(me);
        if (i > firstWriter())
            throw new IllegalArgumentException("Lock not held");
        RWNode n = (RWNode)m_rWaiter.elementAt(i);
        n.drop();
        if (n.isFree())
        {
            m_rWaiter.removeElementAt(i);
            notifyAll();
        }
    }

    private Vector m_rWaiter = new Vector();

    private class RWNode
    {
        static final int READER = 0;
        static final int WRITER = 1;
        private Thread m_t;
        private int m_state;
        private int m_cAcquire = 0;
        RWNode(Thread t, int state)
        {
            m_t = t;
            m_state = state;
        }
        boolean isWriter() { return m_state==WRITER; }
        boolean isFor(Thread t) { return m_t==t; }
        void acquire() { ++m_cAcquire; }
        void drop() { --m_cAcquire; }
        boolean isFree() { return m_cAcquire==0; }
    }

    private synchronized void lock(int state)
    {
        Thread me = Thread.currentThread();
        int i = getIndex(me);
        RWNode n;
        if (i==-1)
        {
            n = new RWNode(me,state);
            m_rWaiter.addElement(n);
        }
        else
        {
            n = (RWNode)m_rWaiter.elementAt(i);
            if (state==RWNode.WRITER && !n.isWriter())
                throw new IllegalArgumentException(
                    "Cannot upgrade from a read lock to a write lock.");
        }
        while (getIndex(me) > (state==RWNode.WRITER ? 0 : firstWriter()))
        {
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
            }
        }
        n.acquire();
    }

    private int firstWriter()
    {
        int i = 0;
        for (Enumeration e = m_rWaiter.elements(); e.hasMoreElements(); ++i)
        {
            RWNode n = (RWNode)e.nextElement();
            if (n.isWriter())
                return i;
        }
        return Integer.MAX_VALUE;
    }

    private int getIndex(Thread t)
    {
        int i = 0;
        for (Enumeration e = m_rWaiter.elements(); e.hasMoreElements(); ++i)
        {
            RWNode n = (RWNode)e.nextElement();
            if (n.isFor(t))
                return i;
        }
        return -1;
    }
}
