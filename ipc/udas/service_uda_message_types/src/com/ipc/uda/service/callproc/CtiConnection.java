package com.ipc.uda.service.callproc;

import com.ipc.uda.service.locator.ServiceLocatorProvider;
import com.ipc.uda.service.util.CtiUtil;
import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.CtiException;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.UserData;
import com.ipc.va.cti.callControl.services.extensions.ClearConnectionExtensionsImpl;
import com.ipc.va.cti.callControl.services.extensions.GenerateDigitsExtensionsImpl;
import com.ipc.va.cti.callControl.services.extensions.HoldCallExtensionsImpl;
import com.ipc.va.cti.callControl.services.results.SetCallSignalingResult;

/**
 * Represents a connection to a call. "Connection" and "call" are terms defined by
 * the CTI layer.
 * 
 * @author mosherc
 */
public class CtiConnection
{
    private final ConnectionID id;

    /**
     * Creates a connection representation, given the connection's ID.
     * @param id the "Connection ID" of this connection
     */
    public CtiConnection(final ConnectionID id)
    {
        this.id = id;
    }

    /**
     * Gets this connection's ID.
     * @return the "Connection ID" of this connection
     */
    public ConnectionID getID()
    {
        return this.id;
    }

    /**
     * Calls the CTI call control service clearConnection, which removes our
     * connection from its call.
     * 
     * @param cause cause for clearConnection
     * @param userData user data for clearConnection
     * @throws CtiException if any exception happens in CTI
     */
    public void clear(final EventCause cause, final UserData userData) throws CtiException
    {
        try
        {
            final ClearConnectionExtensionsImpl extensions = new ClearConnectionExtensionsImpl();
            ServiceLocatorProvider.getCtiServiceLocator().getCallControlService().
                clearConnection(this.id,userData,cause,extensions);
        }
        catch (final Throwable e)
        {
            wrapException(e);
        }
    }

    /**
     * Calls the CTI call control service holdCall, to place the call
     * that this connection is for "on hold".
     * 
     * @throws CtiException if any exception happens in CTI
     */
    public void hold() throws CtiException
    {
        try
        {
            final HoldCallExtensionsImpl extensions = new HoldCallExtensionsImpl();
            ServiceLocatorProvider.getCtiServiceLocator().getCallControlService().
                holdCall(this.id,extensions);
        }
        catch (final Throwable e)
        {
            wrapException(e);
        }
    }

    /**
     * Sends digits to CTI to "dial" on this connection. If this connection is
     * for a conference call, then CTI is responsible for sending the digits to
     * the "last" line added to the conference (not to all lines in the conference).
     * 
     * @param digits the digits to send to the phone line
     * @throws CtiException if any exception happens in CTI
     */
    public void dialDigits(final String digits) throws CtiException
    {
        try
        {
            // TODO should we get tone duration from the client?
            final int toneDuration = 250;
            final GenerateDigitsExtensionsImpl extensions = new GenerateDigitsExtensionsImpl();
            ServiceLocatorProvider.getCtiServiceLocator().getCallControlService().
                generateDigits(this.id,digits, toneDuration, extensions);
        }
        catch (final Throwable e)
        {
            wrapException(e);
        }
    }

    /**
     * Does a SIGNAL on this connection (which only makes sense for MRD private wire calls).
     * @param signal <code>true</code> to SIGNAL, <code>false</code> to stop SIGNAL
     * @throws CtiException if any exception happens in CTI
     */
    public void signal(final boolean signal) throws CtiException
    {
        try
        {
            final SetCallSignalingResult result = ServiceLocatorProvider.getCtiServiceLocator().getCallControlService().
                setCallSignaling(this.id,signal);
            CtiUtil.checkCtiResultStatus(result);
        }
        catch (final Throwable e)
        {
            wrapException(e);
        }
    }

    /**
     * Converts the given exception to a CtiException. If the given exception is an instance
     * of CtiException, then just (re-)throw it, Otherwise, throw a new CtiException whose
     * cause is the given exception.
     * @param e the exception to wrap
     * @throws CtiException always
     */
    private static void wrapException(final Throwable e) throws CtiException
    {
        if (e instanceof CtiException)
        {
            throw (CtiException)e;
        }
        throw new CtiException(e);
    }

    /**
     * Two CtiConnections are considered equal if they have the same ID.
     */
    @Override
    public boolean equals(final Object object)
    {
        if (!(object instanceof CtiConnection))
        {
            return false;
        }
        final CtiConnection that = (CtiConnection)object;
        return this.id.getConnectionID().equals(that.id.getConnectionID());
    }

    /**
     * Two CtiConnections are considered equal if they have the same ID.
     */
    @Override
    public int hashCode()
    {
        return this.id.getConnectionID().hashCode();
    }
}
