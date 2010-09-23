/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;


import com.ipc.ds.entity.dto.Button.EnumIncomingActionPriority;
import com.ipc.uda.event.ExecutableResultQueue;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.locator.ServiceLocatorProvider;
import com.ipc.uda.service.util.CtiUtil;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.service.util.smc.Actions;
import com.ipc.uda.types.BargeInUpdatedEvent;
import com.ipc.uda.types.BargedInUpdatedEvent;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.CenterlineMessageEvent;
import com.ipc.uda.types.CliUpdatedEvent;
import com.ipc.uda.types.CpiUpdatedEvent;
import com.ipc.uda.types.Event;
import com.ipc.uda.types.LineStatusType;
import com.ipc.uda.types.LineStatusUpdatedEvent;
import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.DeviceIDImpl;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.ResultStatusType;
import com.ipc.va.cti.UserDataImpl;
import com.ipc.va.cti.callControl.AudioDeviceChannelImpl;
import com.ipc.va.cti.callControl.AudioDeviceType;
import com.ipc.va.cti.callControl.CallType;
import com.ipc.va.cti.callControl.CallUsage;
import com.ipc.va.cti.callControl.CallUsageAttribute;
import com.ipc.va.cti.callControl.ConferenceActionType;
import com.ipc.va.cti.callControl.HandsetSide;
import com.ipc.va.cti.callControl.events.CallControlConferencedEvent;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;
import com.ipc.va.cti.callControl.services.CallControlServices;
import com.ipc.va.cti.callControl.services.extensions.CancelTelephonyTonesExtensionsImpl;
import com.ipc.va.cti.callControl.services.extensions.ConferenceCallExtensionsImpl;
import com.ipc.va.cti.callControl.services.extensions.GenerateDigitsExtensionsImpl;
import com.ipc.va.cti.callControl.services.extensions.MakeCallExtensionsImpl;
import com.ipc.va.cti.callControl.services.results.ConferenceCallResult;
import com.ipc.va.cti.callControl.services.results.GenerateDigitsResult;
import com.ipc.va.cti.callControl.services.results.MakeCallResult;
import com.ipc.va.cti.callControl.services.results.SeizeResourceResult;
import com.ipc.va.cti.callControl.services.results.SetCallSignalingResult;
import com.ipc.va.cti.logicalDevice.CDIClientType;

/**
 * @author mosherc
 * @author sharmar
 */
public class PrivateWireActions extends PrivateWireNonMockableActions implements
        PrivateWireMockableActions
{
    private static final int DIGIT_TONE_DURATION = 250;
    private final CallControlServices svcCallControl;
    private final PrivateWireCall privateWireCall;
    private final boolean initiateCallOnSeize;

    public PrivateWireActions(final PrivateWireCall privateWireCall,
            final ButtonAppearance appearance, final UdaButton udaButton, final boolean initiateCallOnSeize)
    {
        super(appearance, udaButton);
        this.privateWireCall = privateWireCall;
        this.svcCallControl = ServiceLocatorProvider.getCtiServiceLocator().getCallControlService();
        this.initiateCallOnSeize = initiateCallOnSeize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.PrivateWireMockableActions#ctiMakeCall()
     */
    @Override
    public void ctiMakeCall()
    {
        try
        {
            final DeviceID callingParty = new DeviceIDImpl(this.appearance.getAor());
            final DeviceID calledParty = new DeviceIDImpl(getCalledParty());
            final AudioDeviceChannelImpl audioChannel = new AudioDeviceChannelImpl();

            // TODO hardcoding to LEFT handset for now
            audioChannel.setHandsetSide(HandsetSide.LEFT);

            final MakeCallExtensionsImpl ext = new MakeCallExtensionsImpl();
            ext.setCallType(CallType.RESOURCE);
            ext.setCDIClientType(CDIClientType.UDA);
            ext.setAudioDeviceType(AudioDeviceType.HANDSET);
            ext.setAudioDeviceChannel(audioChannel);
            CallUsageAttribute attribute = new CallUsageAttribute(Integer.toString(this.appearance
                    .getAppearance()));
            ext.setCallUsageAttribute(attribute);
            ext.setCallUsage(CallUsage.APPEARANCE);
            ext.setUserAOR(ctx().getUser().getName());

            final UserDataImpl userData = new UserDataImpl();
            userData.setIdentity(ctx().getUserName());
            userData.setExtension(ctx().getPersonalExtension());

            final MakeCallResult resultMakeCall = this.svcCallControl.makeCall(callingParty,
                    calledParty, userData, EventCause.MAKE_CALL, ext);
            this.digits = "";
            CtiUtil.checkCtiResultStatus(resultMakeCall);

            // TODO hardcoding to LEFT handset for now
            ctx().getCallContext().getLeft().newConnection(resultMakeCall.getConnectionId());
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    private String getCalledParty()
    {
        if (this.digits == null || this.digits.isEmpty())
        {
            return this.appearance.getAor();
        }
        return this.digits;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.PrivateWireMockableActions#putOntoHandset()
     */
    @Override
    public void putOntoHandset()
    {
        ctx().getCallContext().getLeft().addCall(this.privateWireCall);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.PrivateWireMockableActions#removeFromHandset()
     */
    @Override
    public void removeFromHandset()
    {
        ctx().getCallContext().getLeft().removeCall(this.privateWireCall);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ipc.uda.service.callproc.PrivateWireMockableActions#udacLineStatusUpdated(com.ipc.uda
     * .types.LineStatusType)
     */
    @Override
    public void udacLineStatusUpdated(final LineStatusType status)
    {
        try
        {
            // send line status event to UDAC on the event-channel
            final LineStatusUpdatedEvent lineStatusEvent = new LineStatusUpdatedEvent();

            lineStatusEvent
                    .setLineStatus(status == LineStatusType.HI_RING ? incommingPriorityCall()
                            : status);

            lineStatusEvent.setLineStatus(status);
            lineStatusEvent.setButtonId(this.udaButton.getId());
              
            final Event event = new Event();
            event.setLineStatusUpdated(lineStatusEvent);
            
            ExecutableResultQueue.<Event> send(event, ctx().getUser(), ctx().getUserID()
                    .getDeviceID());
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    private LineStatusType incommingPriorityCall()
    {
        EnumIncomingActionPriority incomingActionPriority = this.udaButton.getDsButton()
                .getIncomingActionPriority();
        if (incomingActionPriority == EnumIncomingActionPriority.high)
        {
            return LineStatusType.HI_RING;
        }
        return LineStatusType.LO_RING;
    }

    @Override
    public boolean initiateCallOnSeize()
    {
        return this.initiateCallOnSeize;
    }

    private UserContext ctx()
    {
        return this.privateWireCall.getContext();
    }

    @Override
    public void ctiConference(final ConferenceActionType action)
    {
        // TODO hardcode LEFT handset for now
        final ConnectionID connectionID = ctx().getCallContext().getLeft().getConnection().getID();

        final ConferenceCallExtensionsImpl extensions = new ConferenceCallExtensionsImpl();
        extensions.setConferenceActionType(action);
        extensions.setAppeareance(this.appearance.getAppearance());
        extensions.setResourceAOR(this.appearance.getAor());

        try
        {
            final ConferenceCallResult resultConfCall = this.svcCallControl.conferenceCall(connectionID,extensions);
            CtiUtil.checkCtiResultStatus(resultConfCall);            
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }
    
    @Override
    public void ctiReleaseResource()
    {
        final String userAOR = ctx().getUser().getName();
        final String resourceAOR = this.appearance.getAor();
        final int appear = this.appearance.getAppearance();
        
        final AudioDeviceChannelImpl audioChannel = new AudioDeviceChannelImpl();
        // TODO hard-code left handset for now
        audioChannel.setHandsetSide(HandsetSide.LEFT);
        
        try
        {
            this.svcCallControl.releaseResource(userAOR, resourceAOR, appear, AudioDeviceType.HANDSET, audioChannel);
            // ignore any failure to release the resource (because we expect failure to happen sometimes).
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }  
    }

    @Override
    public void ctiSeizeResource(final PrivateWireFsmContext fsm)
    {
        try
        {
            final String userAOR = ctx().getUser().getName();
            final String resourceAOR = this.appearance.getAor();
            final int appear = this.appearance.getAppearance();

            final AudioDeviceChannelImpl audioChannel = new AudioDeviceChannelImpl();
            // TODO hard-code left handset for now
            audioChannel.setHandsetSide(HandsetSide.LEFT);
    
            final SeizeResourceResult result = this.svcCallControl.seizeResource(
                userAOR, resourceAOR, appear, false, AudioDeviceType.HANDSET, audioChannel);
            final ResultStatusType resStatusType = result.getResultStatus();
            if (resStatusType.equals(ResultStatusType.FAILURE))
            {
                fsm.seizeFailed(result);

                CenterlineMessageEvent centerlineMessageEvent= new CenterlineMessageEvent();
                centerlineMessageEvent.setMessage("What Failure Message have to send ? ");
            }
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    @Override
    public void udacBarge()
    {
        try
        {
            // send "barge" event to UDAC on the event-channel
            final BargeInUpdatedEvent barge = new BargeInUpdatedEvent();
            barge.setButtonId(this.udaButton.getId());
            final Event eventB = new Event();
            eventB.setBargeInUpdated(barge);

            ExecutableResultQueue.<Event>send(
                eventB,
                ctx().getUser(),
                ctx().getUserID().getDeviceID());
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    @Override
    public void udacBarged(CallControlConferencedEvent conferenced)
    {
        try
        {
            // send "barged" event to UDAC on the event-channel
            final BargedInUpdatedEvent barged = new BargedInUpdatedEvent();
            barged.setButtonId(this.udaButton.getId());
            barged.setName(conferenced.getUserData().getIdentity());

            final Event event = new Event();
            event.setBargedInUpdated(barged);

            ExecutableResultQueue.<Event>send(
                event,
                ctx().getUser(),
                ctx().getUserID().getDeviceID());
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    @Override
    public boolean isConference()
    {
        // TODO hard-coding left handset for now
        return ctx().getCallContext().getLeft().isConference();
    }

    @Override
    public void ctiCancelTelephonyTones()
    {
        final AudioDeviceChannelImpl channel = new AudioDeviceChannelImpl();
        // TODO hard-coding LEFT HANDSET for now
        channel.setHandsetSide(HandsetSide.LEFT);

        final CancelTelephonyTonesExtensionsImpl extensions = new CancelTelephonyTonesExtensionsImpl();
        extensions.setAudioDeviceChannel(channel);
        extensions.setAudioDeviceType(AudioDeviceType.HANDSET);

        extensions.setUserAOR(ctx().getUser().getName());

        try
        {
            this.svcCallControl.cancelTelephonyTones(extensions);
        }
        catch (final Throwable e)
        {
            Log.logger().debug("CancelTelephonyTones failed.",e);
        }
    }

    @Override
    public void udacCliUpdate(String cliName, String cliNumber)
    {
       try
       {
           final CliUpdatedEvent cliUpdateEvent = new CliUpdatedEvent();
           cliUpdateEvent.setButtonId(this.udaButton.getId());
           cliUpdateEvent.setName(cliName);
           cliUpdateEvent.setNumber(cliNumber);
        
           final Event event = new Event();
           event.setCliUpdated(cliUpdateEvent);
        
           ExecutableResultQueue.<Event> send(event, ctx().getUser(), ctx().getUserID()
                .getDeviceID());
       }
       catch (final Throwable e)
       {
           Actions.handleActionException(e);
       }
       
    }

    @Override
    public void udacCpiUpdate(CallControlInformationEvent callControlInfoEvent)
    {
        try
        {
            final CpiUpdatedEvent cpiUpdateEvent = new CpiUpdatedEvent();
            cpiUpdateEvent.setButtonId(this.udaButton.getId());
            cpiUpdateEvent.setName(callControlInfoEvent.getUserData().getIdentity());
            cpiUpdateEvent.setNumber(callControlInfoEvent.getUserData().getExtension());
        
            final Event event = new Event();
            event.setCpiUpdated(cpiUpdateEvent);
        
            ExecutableResultQueue.<Event> send(event, ctx().getUser(), ctx().getUserID()
                    .getDeviceID());    
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    @Override
    public void ctiDialDigits(final String digits)
    {
        // TODO hardcode LEFT handset for now
        final ConnectionID connectionID = ctx().getCallContext().getLeft().getConnection().getID();

        final GenerateDigitsExtensionsImpl extensions = new GenerateDigitsExtensionsImpl();
        try
        {
            final GenerateDigitsResult result = this.svcCallControl.generateDigits(connectionID,digits,DIGIT_TONE_DURATION,extensions);
            CtiUtil.checkCtiResultStatus(result);
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }
}
