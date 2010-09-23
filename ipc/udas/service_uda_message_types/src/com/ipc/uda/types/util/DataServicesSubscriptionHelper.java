/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.ipc.ds.notification.Registrar;
import com.ipc.ds.notification.Subscription;
import com.ipc.ds.notification.adaptors.Adaptor;
import com.ipc.ds.notification.adaptors.JNDIAdaptor;
import com.ipc.uda.event.notification.ds.Topic;
import com.ipc.uda.event.notification.ds.UdaNotifiable;
import com.ipc.uda.event.notification.ds.NotificationListener.Action;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.util.logging.Log;

/**
 * @author mordarsd
 * 
 */
public final class DataServicesSubscriptionHelper
{

    private static final String DS = "ds";
    private static final String JNDI_NAME_ARG = "jndiname";
    private static final String TARGET_ARG = "target";

    /**
     * Creates two subscriptions to the Entity for a certain ID. One subscription for Action.updated
     * and one for Action.deleted.
     * 
     * @param entityName
     * @param id
     * @param userCtx
     */
    public static void createSubscriptionsTo(final String entityName, final int id,
            final UserContext userCtx)
    {
        final StringBuilder topic = new StringBuilder();
        topic.append(DataServicesSubscriptionHelper.DS).append('.').append(entityName).append('.')
                .append(id).append('.').append(Action.updated);

        DataServicesSubscriptionHelper.createSubscriptionTo(topic.toString(), userCtx);

        topic.delete(0, topic.length());
        topic.append(DataServicesSubscriptionHelper.DS).append('.').append(entityName).append('.')
                .append(id).append('.').append(Action.deleted);

        DataServicesSubscriptionHelper.createSubscriptionTo(topic.toString(), userCtx);
    }

    /**
     * 
     * @param topicStr
     * @param userCtx
     */
    public static void createSubscriptionTo(final String topicStr, final UserContext userCtx)
    {

        Log.logger().debug(
                "Creating DS Subscription to Topic: [" + topicStr + "] for User: "
                        + userCtx.getUser());

        final Subscription sub = new Subscription(topicStr, JNDIAdaptor.GetName());

        sub.addAdaptorArg(DataServicesSubscriptionHelper.TARGET_ARG, UdaNotifiable.getInstance());
        sub.addAdaptorArg(DataServicesSubscriptionHelper.JNDI_NAME_ARG,
                UdaNotifiable.JNDI_FULL_NAME);

        Topic topic = null;

        try
        {
            topic = Topic.valueOf(topicStr);
            // First check if the UserContext already contains this Subscription
            if (!userCtx.hasSubscription(topic))
            {
                Registrar.GetRegistrar().registerSubscription(sub);
                userCtx.addDataServiceSubscription(topic, sub);
            }

        }
        catch (final Throwable t)
        {
            throw new IllegalArgumentException(
                    "Unable to add DataServices Subscription on Topic: [" + topicStr + "] for: "
                            + userCtx, t);
        }
    }

    /**
     * Removes the subuscription for the given topic
     * 
     * @param topicStr String
     * @param userCtx UserContext
     */
    public static void removeSubscriptionFor(final String topicStr, final UserContext userCtx)
    {
        Log.logger().debug(
                "Removing DS Subscription for Topic: [" + topicStr + "] for User: "
                        + userCtx.getUser());

        final List<HashMap<Adaptor, LinkedList<Subscription>>> subList = Registrar.GetRegistrar()
                .getSubscriptionsFor(topicStr);

        if (subList == null || subList.isEmpty())
        {
            Log.logger().debug(
                    "There are no subscriptions present for the topic: [" + topicStr
                            + "] for User: " + userCtx.getUser());
        }
        else
        {
            for (final HashMap<Adaptor, LinkedList<Subscription>> subItem : subList)
            {
                final Collection<LinkedList<Subscription>> lSubColl = subItem.values();

                if (lSubColl != null && !lSubColl.isEmpty())
                {
                    for (final LinkedList<Subscription> linkedSubList : lSubColl)
                    {
                        if (linkedSubList != null && !linkedSubList.isEmpty())
                        {
                            for (final Subscription subscription : linkedSubList)
                            {
                                Registrar.GetRegistrar().deregisterSubscription(subscription);
                            }
                        }
                        else
                        {
                            Log.logger().debug(
                                    "There are no subscriptions present for the topic: ["
                                            + topicStr + "] for User: " + userCtx.getUser());
                        }
                    }
                }
                else
                {
                    Log.logger().debug(
                            "There are no subscriptions present for the topic: [" + topicStr
                                    + "] for User: " + userCtx.getUser());
                }
            }
        }
    }

    /**
     * Removes two subscriptions to the Entity for a certain ID. One subscription for Action.updated
     * and one for Action.deleted.
     * 
     * @param entityName String
     * @param id int
     * @param userCtx UserContext
     */
    public static void removeSubscriptionsFor(final String entityName, final int id,
            final UserContext userCtx)
    {
        final StringBuilder topic = new StringBuilder();
        topic.append(DataServicesSubscriptionHelper.DS).append('.').append(entityName).append('.')
                .append(id).append('.').append(Action.updated);

        DataServicesSubscriptionHelper.removeSubscriptionFor(topic.toString(), userCtx);

        topic.delete(0, topic.length());
        topic.append(DataServicesSubscriptionHelper.DS).append('.').append(entityName).append('.')
                .append(id).append('.').append(Action.deleted);

        DataServicesSubscriptionHelper.removeSubscriptionFor(topic.toString(), userCtx);
    }

}
