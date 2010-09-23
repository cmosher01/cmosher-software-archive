/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.notificaton.ds;

import com.ipc.uda.entity.dto.UserContextTransient;
import com.ipc.uda.event.notification.ds.NotificationListener;
import com.ipc.uda.event.notification.ds.Topic;

/**
 * @author mordarsd
 * 
 */
public class UserContextTransientNotificationListener implements NotificationListener
{

    private static final String[] ENTITY_NAMES = { UserContextTransient.class.getSimpleName() };

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.event.notification.ds.NotificationListener#getEntityNames()
     */
    @Override
    public String[] getEntityNames()
    {
        return UserContextTransientNotificationListener.ENTITY_NAMES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.event.notification.ds.NotificationListener#handleNotification(int,
     * com.ipc.uda.event.notification.ds.NotificationListener.Action, java.lang.Object)
     */
    @Override
    public void handleNotification(final Topic topic, final Object context)
    {
        System.out.printf(
                ">>>>> UserContextTransientNotificationListener::handleNotification() - %s, %s\n",
                topic, context);

    }

}
