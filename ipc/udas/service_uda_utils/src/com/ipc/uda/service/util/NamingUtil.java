package com.ipc.uda.service.util;


import javax.naming.Context;

import com.ipc.uda.service.util.logging.Log;



/**
 * Contains static utility methods related to javax.naming package.
 * @author mosherc
 */
public final class NamingUtil
{
    NamingUtil()
    {
        // never instantiated
        throw new IllegalStateException();
    }

    /**
     * Closes the give {@link Context}.
     * @param ctx {@line Context} to close; can be <code>null</code>.
     */
    public static void closeContext(final Context ctx)
    {
        if (ctx == null)
        {
            return;
        }
        try
        {
            ctx.close();
        }
        catch (final Throwable e)
        {
            Log.logger().debug("Exception while closing Context; ignoring the exception.",e);
        }
    }
}
