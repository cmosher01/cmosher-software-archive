package com.ipc.uda.event;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import com.ipc.uda.service.util.logging.Log;

import weblogic.servlet.http.AbstractAsyncServlet;
import weblogic.servlet.http.RequestResponseKey;

public class AsyncServletResponseQueue<T>
{
    private final BlockingQueue<T> msgQueue = new LinkedBlockingQueue<T>();

    private AtomicReference<RequestResponseKey> responseKey = new AtomicReference<RequestResponseKey>();





    public void send(final T msg)
    {
        final boolean added = this.msgQueue.offer(msg);
        if (!added)
        {
            throw new IllegalStateException();
        }

        final RequestResponseKey rrk = this.responseKey.getAndSet(null);

        if (rrk != null)
        {
            notifyServlet(rrk);
        }
    }

    public boolean register(final RequestResponseKey rrk)
    {
        final RequestResponseKey rrkOld = this.responseKey.getAndSet(null);
        if (Log.logger().isDebugEnabled() && rrkOld != null)
        {
            Log.logger().debug("Found an existing RequestResponseKey when none was expected; ignoring the existing key.");
        }

        final boolean notify = !this.msgQueue.isEmpty();

        if (notify)
        {
            notifyServlet(rrk);
        }
        else
        {
            this.responseKey.set(rrk);
        }

        return !notify;
    }

    public void unregister()
    {
        this.responseKey.set(null);
    }

    public T get()
    {
        final T msg = this.msgQueue.poll();
        if (msg == null)
        {
            throw new IllegalStateException();
        }
        return msg;
    }

    private static void notifyServlet(final RequestResponseKey rrk)
    {
        try
        {
            AbstractAsyncServlet.notify(rrk,null);
        }
        catch (final IOException ignore)
        {
            // this could be just a case where the client closed
            // the connection, which is OK; we would just do nothing
            Log.logger().debug("Exception occurred while trying to send long-poll response to client; ignoring it.",ignore);
        }
    }

    public int getPendingCount()
    {
        return this.msgQueue.size();
    }
}
