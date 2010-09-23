/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.notificaton.ds;

import java.util.List;

import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.dto.ContactNote;
import com.ipc.ds.entity.dto.Directory;
import com.ipc.ds.entity.dto.DirectoryGroup;
import com.ipc.ds.entity.dto.DirectoryTree;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.dto.PersonalContactNote;
import com.ipc.ds.entity.dto.PersonalDirectory;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.dto.PointOfContact;
import com.ipc.ds.entity.dto.PreferredPOC;
import com.ipc.ds.entity.dto.PreferredPersonalPOC;
import com.ipc.uda.event.ExecutableResultQueue;
import com.ipc.uda.event.notification.ds.NotificationListener;
import com.ipc.uda.event.notification.ds.Topic;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.ContactType;
import com.ipc.uda.types.Event;
import com.ipc.uda.types.PersonalDirectoryItemRemovedEvent;
import com.ipc.uda.types.PersonalDirectoryItemUpdatedEvent;
import com.ipc.uda.types.UID;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * This class is responsible for handling notifications pertained to update and remove operations of
 * Contact, PersonalContact
 * 
 * @author Veena Makam
 * 
 */
public class ContactNotificationListener implements NotificationListener
{

    private static final String[] ENTITY_NAMES = { Contact.class.getSimpleName(),
            ContactNote.class.getSimpleName(), Directory.class.getSimpleName(),
            DirectoryTree.class.getSimpleName(), DirectoryGroup.class.getSimpleName(),
            PersonalContact.class.getSimpleName(), PersonalContactNote.class.getSimpleName(),
            PersonalDirectory.class.getSimpleName(), PersonalPointOfContact.class.getSimpleName(),
            PointOfContact.class.getSimpleName(), PreferredPOC.class.getSimpleName(),
            PreferredPersonalPOC.class.getSimpleName() };

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.event.notification.ds.NotificationListener#getEntityNames()
     */
    @Override
    public String[] getEntityNames()
    {
        return ContactNotificationListener.ENTITY_NAMES;
    }

    public void handleNotification(final Topic topic, final Object context)
    {

        if (context instanceof PersonalContact)
        {
            this.handlePersonalContact(topic, (PersonalContact) context);
        }
        else
        {
            Log.logger().info(
                    "ContactNotificationListener not handling notification where: " + "Topic = "
                            + topic + ", Object = " + context);

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.event.notification.ds.NotificationListener#handleNotification (int,
     * com.ipc.uda.event.notification.ds.NotificationListener.Action, java.lang.Object)
     */
    private void handlePersonalContact(final Topic topic, final PersonalContact dsPersContact)
    {

        final List<UserContext> subscriberList = UserContextManager.getInstance()
                .getDataServicesSubscribers(topic);

        for (final UserContext subscriber : subscriberList)
        {
            Log.logger().debug(
                    "Found PersonalContact Subscriber for Topic: " + topic + " : " + subscriber);

            ContactType udaContact = new ContactType();
            final Event event = new Event();

            // raise PersonalDirectoryItemUpdatedEvent
            if (topic.getAction() == Action.updated)
            {
                try
                {
                    udaContact = UDAAndDSEntityUtil.mapDSToUDAContact(dsPersContact, udaContact,
                            subscriber);
                }
                catch (final ExecutionException lException)
                {
                    Log.logger().debug("Unable to map the DS entities with the UDA object types",
                            lException);
                }
                final PersonalDirectoryItemUpdatedEvent perCtUpdatedEve = new PersonalDirectoryItemUpdatedEvent();
                perCtUpdatedEve.setContact(udaContact);
                event.setPersonalDirectoryItemUpdated(perCtUpdatedEve);
                ExecutableResultQueue.<Event> send(event, subscriber.getUser(), subscriber
                        .getUserID().getDeviceID());
            }
            // raise PersonalDirectoryItemRemovedEvent
            else if (topic.getAction() == Action.deleted)
            {
                final PersonalDirectoryItemRemovedEvent perCtRemEve = new PersonalDirectoryItemRemovedEvent();
                perCtRemEve.setContactId(new UID("" + topic.getEntityID()));
                event.setPersonalDirectoryItemRemoved(perCtRemEve);
                ExecutableResultQueue.<Event> send(event, subscriber.getUser(), subscriber
                        .getUserID().getDeviceID());
                DataServicesSubscriptionHelper.removeSubscriptionsFor(PersonalContact.class
                        .getSimpleName(), dsPersContact.getId(), subscriber);
            }
            else
            {
                throw new IllegalArgumentException(
                        "Unable to handle Contact notification from DataServices.  "
                                + "Unknown or unsupported Action type for Topic: " + topic);
            }

        }
    }

}
