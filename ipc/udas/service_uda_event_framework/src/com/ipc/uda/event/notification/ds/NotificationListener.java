/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.event.notification.ds;



/**
 * 
 * 
 * @author mordarsd
 * 
 */
public interface NotificationListener
{
    /**
     * Enum representing the different types of Entity notification actions
     * 
     * @author mordarsd
     * 
     */
    public enum Action
    {
        updated, deleted
    }

    /**
     * Handles the Notification
     * 
     * @param topic the topic of this notification
     * @param context the context
     */
    void handleNotification(Topic topic, Object context);

    /**
     * Returns a list of the supported Entity names
     * 
     * @return an array of all supported entity names
     */
    String[] getEntityNames();

}
