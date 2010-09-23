/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.notificaton.ds;

import java.util.List;

import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.ButtonPersonalPointOfContact;
import com.ipc.ds.entity.dto.ButtonPointOfContact;
import com.ipc.ds.entity.dto.ButtonResources;
import com.ipc.uda.event.ExecutableResultQueue;
import com.ipc.uda.event.notification.ds.NotificationListener;
import com.ipc.uda.event.notification.ds.Topic;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.ButtonRemovedEvent;
import com.ipc.uda.types.ButtonUpdatedEvent;
import com.ipc.uda.types.Event;
import com.ipc.uda.types.UID;
import com.ipc.uda.types.util.ButtonUtil;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;

/**
 * This class is responsible to handle all the DS notifications of Button and send the corresponding
 * events to the client
 * 
 * @author Veena Makam
 * 
 */
public class ButtonSheetNotificationListener implements NotificationListener
{
    private static final String[] ENTITY_NAMES = { Button.class.getSimpleName(),
            ButtonPersonalPointOfContact.class.getSimpleName(),
            ButtonPointOfContact.class.getSimpleName(), ButtonResources.class.getSimpleName() };

    @Override
    public String[] getEntityNames()
    {
        return ButtonSheetNotificationListener.ENTITY_NAMES;
    }

    @Override
    public void handleNotification(final Topic topic, final Object context)
    {

        if (context instanceof Button)
        {
            handleNotificationForButton(topic, (Button) context);
        }
        else
        {
            Log.logger().info(
                    "ButtonSheetNotificationListener not handling notification where: "
                            + "Topic = " + topic + ", Object = " + context);
        }
    }

    /**
     * Handles notifications for the Button entity
     * 
     * @param topic Topic
     * @param dsButton Button
     */
    private void handleNotificationForButton(final Topic topic, final Button dsButton)
    {

        final List<UserContext> subscriberList = UserContextManager.getInstance()
                .getDataServicesSubscribers(topic);

        if (subscriberList == null || subscriberList.isEmpty())
        {
            Log.logger().debug("There are no subscribers for the Topic: " + topic);
        }
        else
        {
            // publish the event for each subscriber of the topic
            for (final UserContext usrCtx : subscriberList)
            {
                Log.logger().debug("Found Button Subscriber for Topic: " + topic + " : " + usrCtx);
                final Event eve = new Event();

                // enqueue ButtonUpdatedEvent to the client
                if (topic.getAction() == Action.updated)
                {
                    try
                    {
                        final ButtonUpdatedEvent buttonUpdEve = ButtonUtil
                                .populateButtonUpdatedEvent(usrCtx, dsButton);
                        eve.setButtonUpdated(buttonUpdEve);
                        ExecutableResultQueue.<Event> send(eve, usrCtx.getUser(), usrCtx
                                .getUserID().getDeviceID());
                    }
                    catch (final Exception lException)
                    {
                        Log.logger().debug(
                                "Unable to get the ResourceAOR for the Button id: "
                                        + dsButton.getId());
                    }
                }
                // enqueue SpeakerChannelRemovedEvent to the client
                else if (topic.getAction() == Action.deleted)
                {
                    final ButtonRemovedEvent buttonRemEve = new ButtonRemovedEvent();
                    buttonRemEve.setButtonId(UID.fromDataServicesID(dsButton.getId()));

                    eve.setButtonRemoved(buttonRemEve);
                    ExecutableResultQueue.<Event> send(eve, usrCtx.getUser(), usrCtx.getUserID()
                            .getDeviceID());

                    DataServicesSubscriptionHelper.removeSubscriptionsFor(Button.class
                            .getSimpleName(), dsButton.getId(), usrCtx);
                }
            }
        }
    }
}
