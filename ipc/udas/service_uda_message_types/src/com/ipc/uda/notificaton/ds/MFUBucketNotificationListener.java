/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.notificaton.ds;

import java.util.List;

import com.ipc.ds.entity.dto.MFUBucket;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.dto.PointOfContact;
import com.ipc.uda.event.ExecutableResultQueue;
import com.ipc.uda.event.notification.ds.NotificationListener;
import com.ipc.uda.event.notification.ds.Topic;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.Event;
import com.ipc.uda.types.MfuPointOfContactRemovedEvent;
import com.ipc.uda.types.MfuPointOfContactType;
import com.ipc.uda.types.MfuPointOfContactUpdatedEvent;
import com.ipc.uda.types.UID;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * This class is responsible for handling the notificatiob for MFU Bucket
 * 
 * @author Bhavya Bhat
 * 
 */

// REVIEW take care of formatting

public class MFUBucketNotificationListener implements NotificationListener
{

    private static final String[] ENTITY_NAMES = { MFUBucket.class.getSimpleName(),
            PersonalPointOfContact.class.getSimpleName(), PointOfContact.class.getSimpleName() };

    public String[] getEntityNames()
    {
        return MFUBucketNotificationListener.ENTITY_NAMES;
    }

    public void handleNotification(final Topic topic, final Object context)
    {
        if (context instanceof MFUBucket)
        {
            handleMFUBucketNotification(topic, (MFUBucket) context);
        }
        else
        {
            Log.logger().info(
                    "MFUBucketNotificationListener not handling notification where: " + "Topic = "
                            + topic + ", Object = " + context);

        }
    }

    /**
     * This method is responsible for handling the MFU bucket notification
     * 
     * @param topic
     * @param mfuBucket
     */

    private void handleMFUBucketNotification(final Topic topic, final MFUBucket mfuBucket)
    {
        final List<UserContext> subscribers = UserContextManager.getInstance()
                .getDataServicesSubscribers(topic);
        for (final UserContext subscriber : subscribers)
        {
            Log.logger().debug(
                    "Found MFUBucket Subscriber for Topic: " + topic + " : " + subscriber);

            MfuPointOfContactType mfuPOCype = new MfuPointOfContactType();
            final Event event = new Event();

            if (topic.getAction() == Action.updated)
            {
                try
                {
                    mfuPOCype = UDAAndDSEntityUtil.mapDStoUDAMFUBucket(mfuBucket, subscriber);
                }
                catch (final ExecutionException e)
                {
                    Log.logger()
                            .debug("Unable to map the DS entities with the UDA object types", e);
                }
                catch (final Exception e)
                {
                    Log.logger()
                            .debug("Unable to map the DS entities with the UDA object types", e);
                }

                final MfuPointOfContactUpdatedEvent mfuUpdatedEvent = new MfuPointOfContactUpdatedEvent();
                mfuUpdatedEvent.setMfuPointOfContact(mfuPOCype);
                event.setMfuPointOfContactUpdated(mfuUpdatedEvent);
                ExecutableResultQueue.<Event> send(event, subscriber.getUser(), subscriber
                        .getUserID().getDeviceID());

            }
            else if (topic.getAction() == Action.deleted)
            {
                final MfuPointOfContactRemovedEvent mfuRemovedEvent = new MfuPointOfContactRemovedEvent();
                mfuRemovedEvent.setContactId(UID.fromDataServicesID(topic.getEntityID()));
                event.setMfuPointOfContactRemoved(mfuRemovedEvent);
                ExecutableResultQueue.<Event> send(event, subscriber.getUser(), subscriber
                        .getUserID().getDeviceID());
                DataServicesSubscriptionHelper.removeSubscriptionsFor(MFUBucket.class
                        .getSimpleName(), mfuBucket.getId(), subscriber);

            }
            else
            {
                throw new IllegalArgumentException(
                        "Unable to handle MFUBucket notification from DataServices.  "
                                + "Unknown or unsupported Action type for Topic: " + topic);
            }
        }
    }

}
