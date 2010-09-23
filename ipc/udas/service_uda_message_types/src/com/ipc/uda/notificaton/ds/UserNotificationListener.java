/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.notificaton.ds;

import com.ipc.ds.entity.dto.User;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserUDA;
import com.ipc.uda.event.notification.ds.NotificationListener;
import com.ipc.uda.event.notification.ds.Topic;

/**
 * @author mordarsd
 */
public class UserNotificationListener implements NotificationListener
{

    private static final String[] ENTITY_NAMES = { User.class.getSimpleName(),
            UserCDI.class.getSimpleName(), UserUDA.class.getSimpleName() };

    @Override
    public String[] getEntityNames()
    {
        return UserNotificationListener.ENTITY_NAMES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.event.notification.NotificationListener#handleNotification(int,
     * java.lang.Object)
     */
    @Override
    public void handleNotification(final Topic topic, final Object context)
    {
        // TODO Auto-generated method stub

    }
}
