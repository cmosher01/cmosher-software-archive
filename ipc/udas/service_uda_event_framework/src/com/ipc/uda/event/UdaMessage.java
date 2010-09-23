package com.ipc.uda.event;

import java.io.Serializable;

import com.ipc.uda.service.util.UdaPrincipal;

/**
 * This class augments a {@link Serializable} object with
 * a {@link UdaPrincipal} that is associated with the object.
 * 
 * @param <T> the Serializable type of the object to augment
 * 
 * @author mosherc
 */
public class UdaMessage<T extends Serializable> implements Serializable
{
    private final String deviceID;
    private final UdaPrincipal user;
    private final T message;

    /**
     * Initializes a new UdaMessage with the given associated user
     * and serializable object to associate.
     * @param user the {@link UdaPrincipal} the message is for
     * @param deviceID  the deviceID (of the user) that the message is for
     * @param message the message itself to send to the user's device
     */
    public UdaMessage(final UdaPrincipal user, final String deviceID, final T message)
    {
        this.user = user;
        this.deviceID = deviceID;
        this.message = message;
    }
    
    /**
     * Gets the UdaPrincipal that the message is associated with.
     * @return the UdaPrincipal this message is for
     */
    public UdaPrincipal getUser()
    {
        return this.user;
    }

    /**
     * Gets the DeviceID that the message is associated with.
     * @return the user
     */
    public String getDeviceID()
    {
        return this.deviceID;
    }

    /**
     * Gets the message itself.
     * @return the message
     */
    public T getMessage()
    {
        return this.message;
    }
}
