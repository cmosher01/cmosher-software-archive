package com.ipc.uda.service.servlet;

import java.io.BufferedWriter;

import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.Event;
import com.ipc.uda.types.TimeoutEvent;

final class EventControllerHelper extends ControllerHelper
{
    EventControllerHelper()
    {
        // package level access
    }

    public void longpoll(final UserContext user, final boolean timeout, final BufferedWriter out)
    {
        final Event event = getResponseOrTimeout(user,timeout);
        if (Log.logger().isDebugEnabled())
        {
            Log.logger().debug("Sending event to user "+user.getUser().getName());
        }
        serialize(event,out);
    }

    private Event getResponseOrTimeout(final UserContext user, final boolean timeout)
    {
        user.getEventQueue().unregister();
        if (timeout)
        {
            return createTimeoutEvent();
        }
        return user.getEventQueue().get();
    }

    private static Event createTimeoutEvent()
    {
        final TimeoutEvent timeoutEvent = new TimeoutEvent();
        timeoutEvent.setMessage("No events occurred within the configured timeout period.");
        timeoutEvent.setSource("UDAS");

        final Event event = new Event();
        event.setTimeout(timeoutEvent);

        return event;
    }
}
