/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;


import com.ipc.uda.event.ExecutableResultQueue;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.locator.CtiServiceLocator;
import com.ipc.uda.service.locator.ServiceLocatorProvider;
import com.ipc.uda.service.util.CtiUtil;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.service.util.smc.Actions;
import com.ipc.uda.types.Event;
import com.ipc.uda.types.IcmStatusUpdatedEvent;
import com.ipc.uda.types.LineStatusType;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.DeviceIDImpl;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.ResultStatusType;
import com.ipc.va.cti.UserDataImpl;
import com.ipc.va.cti.callControl.AudioDeviceChannel;
import com.ipc.va.cti.callControl.AudioDeviceChannelImpl;
import com.ipc.va.cti.callControl.AudioDeviceType;
import com.ipc.va.cti.callControl.CallType;
import com.ipc.va.cti.callControl.HandsetSide;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;
import com.ipc.va.cti.callControl.services.CallControlServices;
import com.ipc.va.cti.callControl.services.extensions.AnswerCallExtensionsImpl;
import com.ipc.va.cti.callControl.services.extensions.MakeCallExtensionsImpl;
import com.ipc.va.cti.callControl.services.results.AcceptCallResult;
import com.ipc.va.cti.callControl.services.results.AnswerCallResult;
import com.ipc.va.cti.callControl.services.results.MakeCallResult;
import com.ipc.va.cti.callControl.services.results.SendUserInformationResult;
import com.ipc.va.cti.logicalDevice.CDIClientType;


/**
 * Contains the production implementation of the mockable actions
 * called by the IcmP2P FSM.
 * 
 * @see IcmP2P
 * @author mosherc
 */
public class IcmP2PActions extends IcmP2PNonMockableActions implements IcmP2PMockableActions
{
    private final CallControlServices svcCallControl;
    private final IcmP2PCall icm;

    



    public IcmP2PActions(final IcmP2PCall icm)
    {
        this.icm = icm;
        final CtiServiceLocator ctiLocator = ServiceLocatorProvider.getCtiServiceLocator();
        this.svcCallControl = ctiLocator.getCallControlService();
    }




    /**
     * Accepts the incoming call and subscribes for any status changes to the line/call
     */
    @Override
    public void ctiAccept()
    {
        try
        {
            final AcceptCallResult resultAcceptCall = this.svcCallControl.acceptCall(this.connectionID,null,null);
            CtiUtil.checkCtiResultStatus(resultAcceptCall);
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    private UserContext ctx()
    {
        return this.icm.getContext();
    }

    public void putOntoHandset()
    {
        ctx().getCallContext().getLeft().addCall(this.icm);
    }

    public void removeFromHandset()
    {
        ctx().getCallContext().getLeft().removeCall(this.icm);
    }

    /**
     * Answers the incoming call, and also checks for status updates
     */
    @Override
    public void ctiAnswer()
    {
    	try
    	{
    	    final UserDataImpl userData = new UserDataImpl();
            userData.setIdentity(ctx().getUserName());
            userData.setExtension(ctx().getPersonalExtension());

            final AnswerCallExtensionsImpl ext = new AnswerCallExtensionsImpl();
            ext.setAudioDeviceType(AudioDeviceType.HANDSET);
            final AudioDeviceChannel channel = new AudioDeviceChannelImpl();
            channel.setHandsetSide(HandsetSide.LEFT); // TODO right or left handset
            ext.setAudioDeviceChannel(channel);

            final AnswerCallResult answerCallResult = this.svcCallControl.answerCall(this.connectionID,userData,ext);
    		CtiUtil.checkCtiResultStatus(answerCallResult);

    		final SendUserInformationResult result = this.svcCallControl.sendUserInformation(this.connectionID,userData);
    		if (result.getResultStatus().equals(ResultStatusType.FAILURE))
    		{
    		    Log.logger().info("Failed to send user information: "+userData.getIdentity());
    		}
    	}
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    /**
     * On call complete,necessary cleanup is done
     */
    @Override
    public void ctiClearConnection(final EventCause cause)
    {
    	try
    	{
    		this.svcCallControl.clearConnection(this.connectionID, null, cause, null);
    	}
    	catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    /**
     * initiates a make call on CTI Interface 
     */
    @Override
    public void ctiMakeCall()
    {
        try
        {
            final DeviceID callingParty = new DeviceIDImpl(ctx().getUser().getName());
            final DeviceID calledParty = new DeviceIDImpl(this.aor);
            final AudioDeviceChannelImpl audioChannel = new AudioDeviceChannelImpl();
            
            // Note: hardcoding to LEFT handset for now
            audioChannel.setHandsetSide(HandsetSide.LEFT);

            final MakeCallExtensionsImpl ext = new MakeCallExtensionsImpl();
            ext.setCallType(CallType.INTERCOM_POINT_TO_POINT);
            ext.setCDIClientType(CDIClientType.UDA);
            ext.setAudioDeviceType(AudioDeviceType.HANDSET);
            ext.setAudioDeviceChannel(audioChannel);
            ext.setUserAOR(ctx().getUser().getName());

            final UserDataImpl userData = new UserDataImpl();
            userData.setIdentity(ctx().getUserName());
            userData.setExtension(ctx().getPersonalExtension());
            
            final MakeCallResult resultMakeCall = this.svcCallControl.makeCall(callingParty, calledParty, userData, EventCause.MAKE_CALL, ext);
            CtiUtil.checkCtiResultStatus(resultMakeCall);
            this.connectionID = resultMakeCall.getConnectionId();

//            ctx().getCallContext().getIcmApp().getMapConnectionIdToIntercom().put(this.connectionID,this.icm);
            
            // Add the ConnectionID to the UserContextManagers map as well
            final ConferenceCall conference = ctx().getCallContext().getLeft(); // TODO hardcode left handset, for now
            UserContextManager.getInstance().getConnectionManager().put(new CtiConnection(this.connectionID),conference);
            
            // Also need to add this ConnectionID/UserContext mapping
//            UserContextManager.getInstance().setUserContext(this.connectionID, ctx());
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    /**
     * Notifies UDAC about the line status updates through standard event channel from UDAS to UDAC
     */
    @Override
    public void udacLineStatusUpdated(final LineStatusType status)
    {
    	final IcmStatusUpdatedEvent lineStatusEvent = new IcmStatusUpdatedEvent();
    	
    	lineStatusEvent.setLineStatus(status);
    	lineStatusEvent.setUserAor(this.aor);
    	
    	final Event event = new Event();    	
    	event.setIcmStatusUpdated(lineStatusEvent);
    	
    	ExecutableResultQueue.<Event> send(event, ctx().getUser(), ctx().getUserID().getDeviceID());
    }




    @Override
    public void destroy()
    {
        ctx().getCallContext().getIcmApp().getMapAorToIntercom().remove(this.aor);
        //ctx().getCallContext().getIcmApp().getMapConnectionIdToIntercom().remove(this.connectionID);
        UserContextManager.getInstance().getConnectionManager().remove(new CtiConnection(this.connectionID));
        
        // Also need to remove this ConnectionID from the UserContext/ConnectionID mapping
//        UserContextManager.getInstance().removeContext(this.connectionID);
    }




    @Override
    public void udacCpiUpdate(CallControlInformationEvent event)
    {
        // TODO send CPI event to UDAC
    }
}
