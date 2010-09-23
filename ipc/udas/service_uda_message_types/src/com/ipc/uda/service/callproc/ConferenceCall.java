package com.ipc.uda.service.callproc;

import java.util.HashSet;
import java.util.Set;

import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent;
import com.ipc.va.cti.callControl.events.CallControlDeliveredEvent;
import com.ipc.va.cti.callControl.events.CallControlEstablishedEvent;
import com.ipc.va.cti.callControl.events.CallControlFailedEvent;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;

/**
 * Represents the current (conference) call on a handset.
 * @author mosherc
 */
public class ConferenceCall
{
    private boolean conference;
    private boolean established;
    private Optional<CtiConnection> connection = new Nothing<CtiConnection>();

    private final Set<Call> calls = new HashSet<Call>();

    /**
     * Returns if this handset is in "conference" mode.
     * @return is in conference mode
     */
    public synchronized boolean isConference()
    {
        return this.conference && exists();
    }

    /**
     * Sets this handset to conference mode. Note that you cannot turn
     * off conference mode once a conference has started.
     */
    public synchronized void turnOn()
    {
        this.conference = true;
    }

    private void turnOff()
    {
        this.conference = false;
    }

    /**
     * Returns <code>true</code> if there are any calls
     * in this conference.
     * @return if this conference has calls
     */
    public synchronized boolean exists()
    {
        return !this.calls.isEmpty();
    }

    public synchronized void removeCall(final Call call)
    {
        this.calls.remove(call);
        if (this.calls.isEmpty())
        {
            turnOff();
        }
    }

    public synchronized void addCall(final Call call)
    {
        // the call being added is already in the conference, so do nothing
        if (this.calls.contains(call))
        {
            return;
        }

        if (isActiveCallReleasable(call))
        {
            release();
        }

        this.calls.add(call);
    }

    /**
     * Determines if there is an active call that should be released
     * before making a new call.
     * If there is already an active call, we need to release it but only under
     * certain conditions: if we are not in conference mode, or if the new call is
     * ICM (and can't be in a conference), or if the existing call is a one-stage
     * dial-tone line that hasn't been connected to an outside line yet (and so its
     * connection ID doesn't exist yet).
     * 
     * @param callAttempted the new call being made
     * @return true if there is an active call that should be released
     * before making the new call
     */
    private boolean isActiveCallReleasable(final Call callAttempted)
    {
        return !this.conference || !callAttempted.canConference() || !this.connection.exists();
    }

    /**
     * Determines if there is a line call (private wire or dial-tone)
     * currently active.
     * @return true if there is an active line call
     */
    public synchronized boolean hasLineCall()
    {
        for (final Call call : this.calls)
        {
            if (call.canConference())
            {
                return true;
            }
        }
        return false;
    }

    public synchronized CtiConnection getConnection()
    {
        return this.connection.get();
    }

    public synchronized void newConnection(final ConnectionID connectionID)
    {
        this.connection = new Optional<CtiConnection>(new CtiConnection(connectionID));
        UserContextManager.getInstance().getConnectionManager().put(this.connection.get(),this);
        UserContextManager.getInstance().getConnectionManager().gc();
    }

    public synchronized void hold()
    {
        this.established = false;
        try
        {
            this.connection.get().hold();
            for (final Call call : this.calls)
            {
                call.udacHold();
            }
        }
        catch (final Throwable e)
        {
            // TODO
            Log.logger().info("error placing call on hold",e);
        }
    }

    public synchronized void release()
    {
        try
        {
            if (this.connection.exists())
            {
                // TODO do we need to add UserData when clearing our connection?
                this.connection.get().clear(this.established ? EventCause.NORMAL_CLEARING : EventCause.CALL_CANCELLED, null);
                this.connection = new Nothing<CtiConnection>();
                this.established = false;
            }
        }
        catch (final Throwable e)
        {
            // TODO
            Log.logger().info("error releasing call",e);
        }

        for (final Call call : this.calls)
        {
            call.release();
        }
    }

    public synchronized void dialDigits(final String digits)
    {
        for (final Call call : this.calls)
        {
            call.udacDigitsDialed(digits);
        }
    }

    public synchronized void cleared(final CallControlConnectionClearedEvent event)
    {
        this.established = false;
        for (final Call call : this.calls)
        {
            call.ctiConnectionCleared(event);
        }
    }

    public synchronized void delivered(CallControlDeliveredEvent event)
    {
        for (final Call call : this.calls)
        {
            call.ctiDelivered(event);
        }
    }

    public synchronized void failed(CallControlFailedEvent event)
    {
        this.established = false;
        for (final Call call : this.calls)
        {
            call.ctiFailed(event);
        }
    }

    public synchronized void established(CallControlEstablishedEvent event)
    {
        this.established = true;
        for (final Call call : this.calls)
        {
            call.ctiEstablished(event);
        }
    }

    public synchronized void callInformationEvent(CallControlInformationEvent event)
    {
        if (this.calls.size() != 1)
        {
            return;
        }
        for (final Call call : this.calls)
        {
            call.ctiCallInformation(event);
        }
    }

    public synchronized void signal(boolean signal)
    {
        try
        {
            if (this.connection.exists())
            {
                this.connection.get().signal(signal);
            }
        }
        catch (final Throwable e)
        {
            // TODO
            Log.logger().info("error signalling the line",e);
        }
    }
}    
