package com.ipc.uda.service.util;

/**
 * Determines if a long-polling client is active. Long polling clients are expected
 * to re-connect soon after disconnected, so if a client does not re-connect within
 * the given timeout period, it is considered inactive. A currently connected client
 * is always considered active. Objects of this class are thread-safe.
 * 
 * @author mosherc
 */
public class ReconnectionTimer
{
    private final long inactivityTimeoutMillis;

    private long lastDisconnection;
    private boolean connected;

    /**
     * Initializes a new timer, with the given timeout.
     * @param inactivityTimeoutMillis inactivity timeout, in milliseconds
     */
    public ReconnectionTimer(final long inactivityTimeoutMillis)
    {
        this.inactivityTimeoutMillis = inactivityTimeoutMillis;
        disconnect();
    }

    /**
     * Informs this timer that the client is currently connected, and so
     * will not time out.
     */
    public synchronized void connect()
    {
        this.connected = true;
    }

    /**
     * Informs this timer that the client is about to disconnect, so effectively
     * starts the timer.
     */
    public synchronized void disconnect()
    {
        this.connected = false;
        this.lastDisconnection = now();
    }

    /**
     * Checks to see if this timer is currently active or not.
     * A timer is inactive if the user last disconnected more than
     * the timeout period ago.
     * @return <code>true</code> if the user is active
     */
    public synchronized boolean isActive()
    {
        return this.connected || now() <= this.lastDisconnection+this.inactivityTimeoutMillis;
    }

    private static long now()
    {
        return System.currentTimeMillis();
    }
}
