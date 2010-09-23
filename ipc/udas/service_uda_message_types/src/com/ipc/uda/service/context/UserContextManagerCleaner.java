package com.ipc.uda.service.context;


import com.ipc.uda.service.util.UdaEnvironmentUtil;
import commonj.timers.Timer;
import commonj.timers.TimerListener;

/**
 * Schedules a recurring task to remove inactive sessions
 * from the {@link UserContextManager}.
 * @author mosherc
 */
public class UserContextManagerCleaner
{
    private static final int FREQUENCY_IN_SECONDS = 60;

    /**
     * Constructs this object to start recurring tasks to remove
     * inactive sessions from the {@link UserContextManager}.
     */
    public UserContextManagerCleaner()
    {
        UdaEnvironmentUtil.getTimerManager().schedule(new TimerListener()
        {
            @Override
            public void timerExpired(final Timer timer)
            {
                UserContextManager.getInstance().removeInactiveSessions();
            }
        },0,UserContextManagerCleaner.FREQUENCY_IN_SECONDS*1000);
    }
}
