/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.notificaton.ds;

import com.ipc.ds.entity.dto.Codec;
import com.ipc.ds.entity.dto.DeviceUDA;
import com.ipc.uda.event.notification.ds.NotificationListener;
import com.ipc.uda.event.notification.ds.Topic;

/**
 * @author mordarsd
 * 
 */
public class DeviceNotificationListener implements NotificationListener
{

    private static final String[] ENTITY_NAMES = { Codec.class.getSimpleName(),
            DeviceUDA.class.getSimpleName() };

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.event.notification.NotificationListener#getEntityNames()
     */
    @Override
    public String[] getEntityNames()
    {
        return DeviceNotificationListener.ENTITY_NAMES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.event.notification.NotificationListener#handleNotification(int,
     * java.lang.Object)
     */
    @Override
    public void handleNotification(final Topic userId, final Object entity)
    {
        // TODO Auto-generated method stub

    }

}
