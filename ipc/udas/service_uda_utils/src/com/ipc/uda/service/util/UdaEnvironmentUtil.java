package com.ipc.uda.service.util;

import javax.naming.Context;
import javax.naming.InitialContext;

import commonj.timers.TimerManager;

/**
 * Contains static methods relating to the environment in which UDA is running (i.e., Weblogic).
 * @author mosherc
 */
public final class UdaEnvironmentUtil
{
    // TODO change the name of this timer?
    private static final String TIMER_JNDI_NAME = "java:comp/env/timer/uda/UserContextTimer";

    UdaEnvironmentUtil()
    {
        throw new IllegalStateException();
    }

    /**
     * Returns a reference to UDA's {@link TimerManager} within Weblogic.
     * @return the {@link TimerManager}
     */
    public static TimerManager getTimerManager()
    {
        Context ctx = null;
        try
        {
            ctx = new InitialContext();

            return (TimerManager)ctx.lookup(UdaEnvironmentUtil.TIMER_JNDI_NAME);
        }
        catch (final Throwable e)
        {
            throw new UdaEnvironmentException("Could not find timer \""+UdaEnvironmentUtil.TIMER_JNDI_NAME+"\"",e);
        }
        finally
        {
            NamingUtil.closeContext(ctx);
        }
    }
}
