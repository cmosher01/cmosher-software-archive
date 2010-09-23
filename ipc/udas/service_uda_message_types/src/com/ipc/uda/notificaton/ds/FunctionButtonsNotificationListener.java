/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */
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
import com.ipc.uda.types.Event;
import com.ipc.uda.types.FunctionButtonRemovedEvent;
import com.ipc.uda.types.FunctionButtonUpdatedEvent;
import com.ipc.uda.types.UID;

/**
 * This class is responsible for handling the notification for function buttons
 * 
 * @author Bhavya Bhat
 * 
 */

public class FunctionButtonsNotificationListener implements
		NotificationListener {

	private static final String[] ENTITY_NAMES = {
			Button.class.getSimpleName(),
			ButtonPersonalPointOfContact.class.getSimpleName(),
			ButtonPointOfContact.class.getSimpleName(),
			ButtonResources.class.getSimpleName() };

	@Override
	public String[] getEntityNames() {
		return FunctionButtonsNotificationListener.ENTITY_NAMES;
	}

	public void handleNotification(final Topic topic, final Object context) {
		if (context instanceof Button) {
			this.handleFunctionButtons(topic, (Button) context);
		} else {
			Log.logger().info(
					"FunctionButtonsNotificationListener not handling notification where: "
							+ "Topic = " + topic + ", Object = " + context);

		}
	}

	/**
	 * This method is responsible for handling the function buttons notification
	 * 
	 * @param topic
	 * @param button
	 */
	private void handleFunctionButtons(final Topic topic, Button button) {

		final List<UserContext> subscriberList = UserContextManager
				.getInstance().getDataServicesSubscribers(topic);

		for (final UserContext subscriber : subscriberList) {
			Log.logger().debug(
					"Found Button Subscriber for Topic: " + topic + " : "
							+ subscriber);

			final Event event = new Event();

			// raise FunctionButtonUpdatedEvent
			if (topic.getAction() == Action.updated) {

				final FunctionButtonUpdatedEvent funcButtonUpdatedEvent = new FunctionButtonUpdatedEvent();
				mapDSButtonTOUDAButton(funcButtonUpdatedEvent, button);
				event.setFunctionButtonUpdated(funcButtonUpdatedEvent);
				ExecutableResultQueue.<Event> send(event, subscriber.getUser(),
						subscriber.getUserID().getDeviceID());
			}
			// raise FunctionButtonRemovedEvent
			else if (topic.getAction() == Action.deleted) {
				final FunctionButtonRemovedEvent funcButtonRemoved = new FunctionButtonRemovedEvent();
				funcButtonRemoved
						.setButtonId(new UID("" + topic.getEntityID()));
				event.setFunctionButtonRemoved(funcButtonRemoved);
				ExecutableResultQueue.<Event> send(event, subscriber.getUser(),
						subscriber.getUserID().getDeviceID());
			} else {
				throw new IllegalArgumentException(
						"Unable to handle Function button notification from DataServices.  "
								+ "Unknown or unsupported Action type for Topic: "
								+ topic);
			}

		}

	}

	/**
	 * This method is responsible for mapping DS button to FunctionButtonUpdatedEvent
	 * 
	 * @param funcButtonUpdatedEvent
	 * @param button
	 */

	private void mapDSButtonTOUDAButton(
			FunctionButtonUpdatedEvent funcButtonUpdatedEvent, Button button) {
		
		//TODO : when XSD is changed move this method to Util class
		
		funcButtonUpdatedEvent.setButtonId(UID.fromDataServicesID(button
				.getId()));
		funcButtonUpdatedEvent.setCanonicalName(button.getButtonLabel());
		funcButtonUpdatedEvent.setType(button.getButtonType().name());
	}

}
