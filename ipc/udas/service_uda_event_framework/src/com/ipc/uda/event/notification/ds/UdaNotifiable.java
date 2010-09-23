/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.event.notification.ds;



import java.util.HashMap;
import java.util.Map;

import com.ipc.ds.notification.Notifiable;
import com.ipc.uda.service.util.logging.Log;



/**
 * 
 * 
 * @author mordarsd
 * 
 */
public final class UdaNotifiable implements Notifiable
{
 
    public static final String JNDI_SUB_CTX = "uda.ds";
    public static final String JNDI_NAME = "Notifiable";
    public static final String JNDI_FULL_NAME = JNDI_SUB_CTX + "." + JNDI_NAME;
    
    private static final UdaNotifiable instance = new UdaNotifiable();

    private final Map<String, NotificationListener> listeners = new HashMap<String, NotificationListener>();



    /**
     * private ctor
     */
    private UdaNotifiable()
    {
        // this is a singleton
    }

    /**
     * Sole instance getter
     * 
     * @return UdaNotifiable The sole instance
     */
    public static UdaNotifiable getInstance()
    {
        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.ds.notification.Notifiable#notify(java.lang.String, java.lang.Object)
     */
    @Override
    public void notify(final String topicStr, final Object context)
    {
    	Log.logger().debug("UdaNotifiable::notify - Topic = " + topicStr + ", Object = " + context);

        final Topic topic = Topic.valueOf(topicStr);

        NotificationListener listener = null;

        synchronized (this.listeners)
        {
            if (this.listeners.containsKey(topic.getEntityName()))
            {
                listener = this.listeners.get(topic.getEntityName());
            }
        }

        if (listener != null)
        {
            listener.handleNotification(topic, context);
        }
    }

    /**
     * Registers a NotificationListener
     * 
     * @param listener
     */
    public void registerNotificationListener(final NotificationListener listener)
    {
        if (listener == null)
        {
            return;
        }

        synchronized (this.listeners)
        {
            for (String name : listener.getEntityNames())
            {
                if (this.listeners.containsKey(name))
                {
                    throw new IllegalStateException("Unable to add NotificationListener: " + listener + " for Entity name: "
                            + name + "; " + "NotificationListener: " + this.listeners.get(name) + " already exists!");
                }
                this.listeners.put(name,listener);
            }
        }
    }
    
    /**
     * 
     * 
     * @param listener
     */
    public void unregisterNotificationListener(final NotificationListener listener)
    {
        if (listener == null)
        {
            return;
        }

        synchronized (this.listeners)
        {
            for (String name : listener.getEntityNames())
            {
                if (this.listeners.containsKey(name))
                {
                    this.listeners.remove(name);
                }
            }
        }
    }

}
