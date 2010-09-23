/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.notificaton.ds;

import java.util.List;

import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.ResourceAORManager;
import com.ipc.uda.event.ExecutableResultQueue;
import com.ipc.uda.event.notification.ds.NotificationListener;
import com.ipc.uda.event.notification.ds.Topic;
import com.ipc.uda.service.callproc.ButtonFactory;
import com.ipc.uda.service.callproc.SpeakerSheet;
import com.ipc.uda.service.callproc.UdaSpeaker;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.Event;
import com.ipc.uda.types.SpeakerChannelRemovedEvent;
import com.ipc.uda.types.SpeakerChannelUpdatedEvent;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.SpeakerUtil;

/**
 * This class is responsible to handle all the DS notifications of UserSpeakerChannel and send the
 * corresponding events to the client
 * 
 * @author Veena Makam
 * 
 */
public class SpeakerNotificationListener implements NotificationListener
{
    private static final String[] ENTITY_NAMES = { UserSpeakerChannel.class.getSimpleName() };

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.event.notification.ds.NotificationListener#getEntityNames()
     */
    @Override
    public String[] getEntityNames()
    {
        return SpeakerNotificationListener.ENTITY_NAMES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ipc.uda.event.notification.ds.NotificationListener#handleNotification(com.ipc.uda.event
     * .notification.ds.Topic, java.lang.Object)
     */
    @Override
    public void handleNotification(final Topic topic, final Object context)
    {

        if (context instanceof UserSpeakerChannel)
        {
            this.handleUserSpeakerChannel(topic, (UserSpeakerChannel) context);
        }
        else
        {
            Log.logger().info(
                    "SpeakerNotificationListener not handling notification where: " + "Topic = "
                            + topic + ", Object = " + context);
        }
    }

    /**
     * Handles UserSpeakerChannel related notifications
     * 
     * @param topic Topic
     * @param usrSprChl UserSpeakerChannel
     */
    private void handleUserSpeakerChannel(final Topic topic, final UserSpeakerChannel usrSprChl)
    {
        // get all the subscribers list for the given topic
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
                // update the SpeakerSheet
                try
                {
                    final SpeakerSheet speakerSheet = usrCtx.getCallContext().getSpeakerSheet();
                    final ResourceAORManager mgrAor = new ResourceAORManager(usrCtx
                            .getSecurityContext());
                    final ResourceAOR aor = mgrAor.getResourceAORFor(usrSprChl);
                    final ButtonAppearance aorAndAppearance = ButtonFactory.createAppearance(aor,
                            usrSprChl.getAppearance());
                    final UdaSpeaker udaSpeaker = new UdaSpeaker(usrSprChl, aorAndAppearance);

                    speakerSheet.addSpeaker(udaSpeaker);
                }
                catch (final Throwable lException)
                {
                    Log.logger().debug(
                            "Unable to get the ResourceAOR for the Speaker Channel id: "
                                    + usrSprChl.getId());
                }

                Log.logger().debug(
                        "Found UserSpeakerChannel Subscriber for Topic: " + topic + " : " + usrCtx);

                final Event eve = new Event();

                // enqueue SpeakerChannelUpdatedEvent to the client
                if (topic.getAction() == Action.updated)
                {
                    final SpeakerChannelUpdatedEvent spUpdEve = SpeakerUtil
                            .populateSpeakerChlUpdEvent(usrCtx, usrSprChl);

                    eve.setSpeakerChannelUpdated(spUpdEve);
                    ExecutableResultQueue.<Event> send(eve, usrCtx.getUser(), usrCtx.getUserID()
                            .getDeviceID());
                }
                // enqueue SpeakerChannelRemovedEvent to the client
                else if (topic.getAction() == Action.deleted)
                {
                    final SpeakerChannelRemovedEvent spRemEve = new SpeakerChannelRemovedEvent();
                    spRemEve.setSpeakerNumber(usrSprChl.getSpeakerNumber());

                    eve.setSpeakerChannelRemoved(spRemEve);
                    ExecutableResultQueue.<Event> send(eve, usrCtx.getUser(), usrCtx.getUserID()
                            .getDeviceID());

                    DataServicesSubscriptionHelper.removeSubscriptionsFor(UserSpeakerChannel.class
                            .getSimpleName(), usrSprChl.getId(), usrCtx);
                }
            }
        }
    }
}
