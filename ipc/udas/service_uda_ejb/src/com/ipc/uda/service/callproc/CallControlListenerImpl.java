/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.callproc;


import java.util.Set;

import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.callControl.CallType;
import com.ipc.va.cti.callControl.ConferenceActionType;
import com.ipc.va.cti.callControl.events.CallControlConferencedEvent;
import com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent;
import com.ipc.va.cti.callControl.events.CallControlDeliveredEvent;
import com.ipc.va.cti.callControl.events.CallControlEstablishedEvent;
import com.ipc.va.cti.callControl.events.CallControlEvent;
import com.ipc.va.cti.callControl.events.CallControlFailedEvent;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;
import com.ipc.va.cti.callControl.events.CallControlListener;
import com.ipc.va.cti.callControl.events.CallControlMutingEvent;
import com.ipc.va.cti.callControl.events.CallControlOfferedEvent;
import com.ipc.va.cti.callControl.events.CallControlOriginatedEvent;
import com.ipc.va.cti.callControl.events.CallControlVoiceActivityEvent;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;

/**
 * @author mordarsd
 *
 */
public class CallControlListenerImpl implements CallControlListener
{

    
    private static final CallControlListenerImpl instance = new CallControlListenerImpl();
    
    private CallControlListenerImpl()
    {
    	// This is a singleton
    }

    /**
     * Sole instance getter
     *     
     * @return
     */
    public static CallControlListenerImpl getInstance()
    {
        return CallControlListenerImpl.instance;
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#bridgedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void bridgedEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#callClearedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void callClearedEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#callInformationEvent(com.ipc.va.cti.callControl.events.CallControlInformationEvent)
     */
    @Override
    public void callInformationEvent(CallControlInformationEvent event)
    {
        log(event);

        // Look up the Call by the ConnectionID in the event 
        final Optional<ConferenceCall> optCall = UserContextManager.getInstance().getConnectionManager().get(event.getConnectionID());
        if (!optCall.exists())
        {
            Log.logger().info(
                    "Received CTI Call Information event for unknown ConnectionID: " + 
                    event.getConnectionID() + ". Ignoring it.");
            return;
        }
        optCall.get().callInformationEvent(event);
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#chargingEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void chargingEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
    }


    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#connectionClearedEvent(com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent)
     */
    @Override
    public void connectionClearedEvent(final CallControlConnectionClearedEvent event)
    {
        log(event);

        // Look up the Call by the ConnectionID in the event 
        final Optional<ConferenceCall> optCall = UserContextManager.getInstance().getConnectionManager().get(event.getConnectionID());
        if (!optCall.exists())
        {
            Log.logger().info(
                    "Received CTI Connection Cleared event for unknown ConnectionID: " + 
                    event.getConnectionID() + ". Ignoring it.");
            return;
        }
        optCall.get().cleared(event);
    }

    private void log(final CallControlEvent event)
    {
        if (!Log.logger().isDebugEnabled())
        {
            return;
        }

        Log.logger().debug("Received "+event.getClass().getSimpleName()+" event from CTI, "+event+", connection ID "+event.getConnectionID());
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#deliveredEvent(com.ipc.va.cti.callControl.events.CallControlDeliveredEvent)
     */
    @Override
    public void deliveredEvent(CallControlDeliveredEvent event)
    {
        log(event);

        // Look up the Call by the ConnectionID in the event 
        final Optional<ConferenceCall> optCall = UserContextManager.getInstance().getConnectionManager().get(event.getConnectionID());
        if (!optCall.exists())
        {
            Log.logger().info(
                    "Received CTI Delivered event for unknown ConnectionID: " + 
                    event.getConnectionID() + ". Ignoring it.");
            return;
        }
        optCall.get().delivered(event);
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#digitsDialedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void digitsDialedEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#digitsGeneratedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void digitsGeneratedEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#divertedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void divertedEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#establishedEvent(com.ipc.va.cti.callControl.events.CallControlEstablishedEvent)
     */
    @Override
    public void establishedEvent(CallControlEstablishedEvent event)
    {
        log(event);
        
        // Look up the Call by the ConnectionID in the event 
        final Optional<ConferenceCall> optCall = UserContextManager.getInstance().getConnectionManager().get(event.getConnectionID());
        if (!optCall.exists())
        {
            Log.logger().info(
                    "Received CTI Delivered event for unknown ConnectionID: " + 
                    event.getConnectionID() + ". Ignoring it.");
            return;
        }
        optCall.get().established(event);
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#failedEvent(com.ipc.va.cti.callControl.events.CallControlFailedEvent)
     */
    @Override
    public void failedEvent(CallControlFailedEvent event)
    {
        log(event);
        final Optional<ConferenceCall> optCall = UserContextManager.getInstance().getConnectionManager().get(event.getConnectionID());
        if (!optCall.exists())
        {
            Log.logger().info(
                    "Received CTI Delivered event for unknown ConnectionID: " + 
                    event.getConnectionID() + ". Ignoring it.");
            return;
        }
        optCall.get().failed(event);
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#heldEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void heldEvent(CallControlEvent event)
    {
        // TODO Probably don't need this event
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#networkCapabilitiesChangedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void networkCapabilitiesChangedEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#networkReachedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void networkReachedEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#offeredEvent(com.ipc.va.cti.callControl.events.CallControlOfferedEvent)
     */
    @Override
    public void offeredEvent(CallControlOfferedEvent event)
    {
        log(event);
        
        // CTI should send us an Offered event only for ICM calls.
        // TODO fix this? assume incoming ICM call is P2P
        if (!(
                event.getCallExtensions().getCallType().equals(CallType.INTERCOM_POINT_TO_POINT) ||
                event.getCallExtensions().getCallType().equals(CallType.INTERCOM_GROUP)))
        {
            Log.logger().info("Received unexpected Offered event from CTI (with AnswerMode not AUTO). Ignoring it.");
            return;
        }

        final String calledUserAOR = event.getCalledDeviceID().getDeviceID();
        //final UdaPrincipal calledUser = new UdaPrincipal(calledUserAOR);
        final Optional<UserContext> userCtx = UserContextManager.getInstance().getUserContext(event.getMonitorCrossRefID());
        if (!userCtx.exists())
        {
            Log.logger().info(
                    "Received CTI Offered event (incoming call) for unknown user: " + calledUserAOR + " ignoring it.");
            return;
        }

        // Note: this Call is not the active call on the (default) left handset
        // until the User has chosen to answer the incoming ICM
        final IcmP2PCall icm = new IcmP2PCall(userCtx.get());
        icm.startFSM();
        userCtx.get().getCallContext().getIcmApp().getMapAorToIntercom().put(event.getCallingDeviceID().getDeviceID(),icm);

        final ConferenceCall conference = new ConferenceCall();
        conference.addCall(icm);
        conference.newConnection(event.getConnectionID());
        UserContextManager.getInstance().getConnectionManager().put(new CtiConnection(event.getConnectionID()),conference);

        icm.ctiOffered(event);
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#originatedEvent(com.ipc.va.cti.callControl.events.CallControlOriginatedEvent)
     */
    @Override
    public void originatedEvent(CallControlOriginatedEvent event)
    {
        // IGNORE THIS EVENT! (at least for now)
        // (per discussion of Leo K. and Chris M.regarding race conditions)

//      /*
//      * We need to handle the Originated event differently from the other CTI events, as far as
//      * using the connection ID to find the call that the event is for. This is due to the fact that
//      * the first time we get the connection ID is as a return value from CTI's makeCall service. The
//      * problem is that CTI sends us an Originated event (with the new call's connection ID) *before*
//      * the makeCall service returns us that connection ID. So we basically get an Originated event
//      * with a connection ID that is unknown to us (yet).
//      */
//        System.out.printf("[%s] >>>>>>>>>>>>>>>>>>>>>>>>>> [%s] originatedEvent: %s\n", 
//                new Date(), Thread.currentThread(), event.getConnectionID());
//
//        
//        // Lookup the UserContext 
//        // *We* are the calling user, so use this trick to get our UserContext:
//        // pull *our* AOR out of the CTI Originated event and use it to look up our UserContext:
//        final String callingUserAOR = event.getCallingDeviceID().getDeviceID();
//        final UdaPrincipal callingUser = new UdaPrincipal(callingUserAOR);
//        final Optional<UserContext> userCtx = UserContextManager.getInstance().getUserContext(callingUser);
//        if (!userCtx.exists())
//        {
//            Log.logger().info(
//                "Received CTI Originated event for unknown user: "+callingUserAOR+
//                ". Ignoring it.");
//            return;
//        }
//
//        // Get the associated Button from the ButtonSheet
//        // Note: if this is part of an ICM, the Button will not be found in the ButtonSheet, 
//        //       but rather in the IcmApplication
//        final Call icm = 
//        	// Do not use the connection ID, because there is an inherent race condition with MakeCall
//            // userCtx.get().getMapConnectionIdToIntercom().get(event.getConnectionID());
//            userCtx.get().getCallContext().getIcmApp().getMapAorToIntercom().get(event.getCalledDeviceID().getDeviceID());
//        
//        icm.CtiOriginated(event);
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#queuedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void queuedEvent(CallControlEvent event)
    {
        // TODO Probably don't need this event
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#retrievedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void retrievedEvent(CallControlEvent event)
    {
        // TODO Probably don't need this event
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#serviceCompletionFailureEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void serviceCompletionFailureEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#serviceInitiatedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void serviceInitiatedEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#telephonyTonesGeneratedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void telephonyTonesGeneratedEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.ipc.va.cti.callControl.events.CallControlListener#transferedEvent(com.ipc.va.cti.callControl.events.CallControlEvent)
     */
    @Override
    public void transferedEvent(CallControlEvent event)
    {
        // TODO Auto-generated method stub
        
    }

	@Override
    public void conferencedEvent(CallControlConferencedEvent event)
    {
        log(event);
        
        // Look up the button(s) by AOR/appearance
        final MonitorCrossRefID xRef = event.getMonitorCrossRefID();
        final Optional<UserContext> optCtx = UserContextManager.getInstance().getUserContext(xRef);
        if (!optCtx.exists())
        {
            Log.logger().info("Received conferenced event for unknown user");
            return;
        }

        final Optional<ButtonAppearance> optAppearance = getAppearance(event);
        if (!optAppearance.exists())
        {
            Log.logger().info("Received conferenced event with invalid AOR/appearance: "+event.getResourceAOR()+"/"+Integer.toString(event.getAppearance()));
            return;
        }

        final Set<ButtonPressCall> calls = optCtx.get().getCallContext().getButtonSheet().getCalls(optAppearance.get());
        if (calls.isEmpty())
        {
            Log.logger().info("Received conferenced event for unknown button: "+optAppearance.get());
            return;
        }
        
        for (final ButtonPressCall call : calls)
        {
            if(EventCause.ACTIVE_PARTICIPATION.equals(event.getEventCause()))
            {
             // send the corresponding barged event to the button(s)
                call.ctiBarged(event);
            }   
            else
            {                
                if(ConferenceActionType.PARTY_ADD.equals(event.getConferenceAction()) 
                        || ConferenceActionType.PARTY_REMOVE.equals(event.getConferenceAction()))
                {
                    // send the corresponding Conference event to the button(s)
                    call.ctiConferenced(event);
                }
            }
        }
    }

    private static Optional<ButtonAppearance> getAppearance(CallControlConferencedEvent event)
    {
        try
        {
            return new Optional<ButtonAppearance>(new ButtonAppearance(event.getResourceAOR(),event.getAppearance()));
        }
        catch (final Throwable e)
        {
            return new Nothing<ButtonAppearance>();
        }
    }

    @Override
    public void mutingEvent(CallControlMutingEvent event)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void voiceActivityEvent(CallControlVoiceActivityEvent event)
    {
        // TODO Auto-generated method stub
        
    }
}
