/**
 * 
 */
package com.ipc.uda.service.callproc;

import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.UserSpeakerChannelManager;
import com.ipc.uda.event.ExecutableResultQueue;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.locator.ServiceLocatorProvider;
import com.ipc.uda.service.util.CtiUtil;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.service.util.smc.Actions;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.CenterlineMessageEvent;
import com.ipc.uda.types.CliUpdatedEvent;
import com.ipc.uda.types.CpiUpdatedEvent;
import com.ipc.uda.types.EnumSpeakerChannelStatusType;
import com.ipc.uda.types.Event;
import com.ipc.uda.types.SpeakerChannelActivityUpdatedEvent;
import com.ipc.uda.types.SpeakerChannelPttFailedEvent;
import com.ipc.uda.types.SpeakerChannelStatusUpdatedEvent;
import com.ipc.uda.types.SpeakerChannelVolumeUpdatedEvent;
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
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;
import com.ipc.va.cti.callControl.services.CallControlServices;
import com.ipc.va.cti.callControl.services.extensions.CancelTelephonyTonesExtensionsImpl;
import com.ipc.va.cti.callControl.services.extensions.GenerateDigitsExtensionsImpl;
import com.ipc.va.cti.callControl.services.extensions.MakeCallExtensionsImpl;
import com.ipc.va.cti.callControl.services.results.GenerateDigitsResult;
import com.ipc.va.cti.callControl.services.results.MakeCallResult;
import com.ipc.va.cti.callControl.services.results.SeizeResourceResult;
import com.ipc.va.cti.logicalDevice.CDIClientType;
import com.ipc.va.cti.physicalDevice.services.PhysicalDeviceServices;

/**
 * @author mosherc
 * @author sharmar
 * 
 */
public class SpeakerActions extends SpeakerNonMockableActions implements SpeakerMockableActions
{
    private static final int DIGIT_TONE_DURATION = 250;

    private final SpeakerCall speakerCall;
    private final CallControlServices svcCallControl;
    private final PhysicalDeviceServices svcPhysicalDevice;

    private final boolean initiateCallOnSeize;
    private final ResourceAOR.EnumType lineDialOrPrivate;

    public SpeakerActions(SpeakerCall speakerCall, ButtonAppearance appearance,
            UdaSpeaker udaSpeaker, boolean initiateCallOnSeize,
            ResourceAOR.EnumType lineDialOrPrivate)
    {
        super(appearance, udaSpeaker);

        this.speakerCall = speakerCall;
        this.svcCallControl = ServiceLocatorProvider.getCtiServiceLocator().getCallControlService();
        this.svcPhysicalDevice = ServiceLocatorProvider.getCtiServiceLocator()
                .getPhysicalDeviceServices();
        this.initiateCallOnSeize = initiateCallOnSeize;
        this.lineDialOrPrivate = lineDialOrPrivate;
    }

    private UserContext ctx()
    {
        return this.speakerCall.getContext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.SpeakerMockableActions#CtiDialDigits(java.lang.String)
     */
    @Override
    public void ctiDialDigits(String digits)
    {
        final ConnectionID connectionID = ctx().getCallContext().getLeft().getConnection().getID();

        final GenerateDigitsExtensionsImpl extensions = new GenerateDigitsExtensionsImpl();
        try
        {
            final GenerateDigitsResult result = this.svcCallControl.generateDigits(connectionID,
                    digits, DIGIT_TONE_DURATION, extensions);
            CtiUtil.checkCtiResultStatus(result);
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.SpeakerMockableActions#CtiSetSpeakerChannelVolume(int)
     */
    @Override
    public void ctiSetSpeakerChannelVolume(int volume)
    {
        final String userAOR = ctx().getUser().getName();
        final AudioDeviceChannelImpl audioChannel = new AudioDeviceChannelImpl();

        audioChannel.setSpeakerNumber(this.udaSpeaker.getDsSpeaker().getSpeakerNumber());

        try
        {
            svcPhysicalDevice.setVolume(userAOR, AudioDeviceType.SPEAKER, audioChannel, volume);
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.SpeakerMockableActions#UdacSpeakerChannelVolumeUpdated(int)
     */
    @Override
    public void udacSpeakerChannelVolumeUpdated(int volume)
    {
        SpeakerChannelVolumeUpdatedEvent spkChnlVolUpdatedEvent = new SpeakerChannelVolumeUpdatedEvent();
        spkChnlVolUpdatedEvent.setSpeakerNumber(this.udaSpeaker.getDsSpeaker().getSpeakerNumber());
        spkChnlVolUpdatedEvent.setLevel(volume);

        final Event event = new Event();
        event.setSpeakerChannelVolumeUpdated(spkChnlVolUpdatedEvent);

        ExecutableResultQueue.<Event> send(event, ctx().getUser(), ctx().getUserID().getDeviceID());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.SpeakerMockableActions#ctiCancelTelephonyTones()
     */
    @Override
    public void ctiCancelTelephonyTones()
    {
        final AudioDeviceChannelImpl channel = new AudioDeviceChannelImpl();
        channel.setSpeakerNumber(this.udaSpeaker.getDsSpeaker().getSpeakerNumber());

        final CancelTelephonyTonesExtensionsImpl extensions = new CancelTelephonyTonesExtensionsImpl();
        extensions.setAudioDeviceChannel(channel);
        extensions.setAudioDeviceType(AudioDeviceType.SPEAKER);

        extensions.setUserAOR(ctx().getUser().getName());

        try
        {
            this.svcCallControl.cancelTelephonyTones(extensions);
        }
        catch (final Throwable e)
        {
            Log.logger().debug("CancelTelephonyTones failed.", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.SpeakerMockableActions#ctiClearConnection()
     */
    @Override
    public void ctiClearConnection(final EventCause cause)
    {
        try
        {
            // TODO fix these parameters
            // final UserDataImpl userData = new UserDataImpl();
            this.svcCallControl.clearConnection(this.connectionID, null, cause, null);
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.SpeakerMockableActions#ctiMakeCall()
     */
    @Override
    public void ctiMakeCall()
    {
        try
        {
            final DeviceID callingParty = new DeviceIDImpl(this.appearance.getAor());
            final DeviceID calledParty = new DeviceIDImpl(getCalledParty());
            final AudioDeviceChannelImpl audioChannel = new AudioDeviceChannelImpl();

            audioChannel.setSpeakerNumber(this.udaSpeaker.getDsSpeaker().getSpeakerNumber());

            final MakeCallExtensionsImpl ext = new MakeCallExtensionsImpl();
            ext.setCallType(CallType.RESOURCE);
            ext.setCDIClientType(CDIClientType.UDA);
            ext.setAudioDeviceType(AudioDeviceType.SPEAKER);
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
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.SpeakerMockableActions#ctiReleaseResource()
     */
    @Override
    public void ctiReleaseResource()
    {
        final String userAOR = ctx().getUser().getName();
        final String resourceAOR = this.appearance.getAor();
        final int appear = this.appearance.getAppearance();

        final AudioDeviceChannelImpl audioChannel = new AudioDeviceChannelImpl();
        audioChannel.setSpeakerNumber(this.udaSpeaker.getDsSpeaker().getSpeakerNumber());

        try
        {
            this.svcCallControl.releaseResource(userAOR, resourceAOR, appear,
                    AudioDeviceType.SPEAKER, audioChannel);
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.SpeakerMockableActions#ctiSeizeResource()
     */
    @Override
    public void ctiSeizeResource(final SpeakerFsmContext fsm)
    {
        try
        {
            final String userAOR = ctx().getUser().getName();
            final String resourceAOR = this.appearance.getAor();
            final int appear = this.appearance.getAppearance();

            final AudioDeviceChannelImpl audioChannel = new AudioDeviceChannelImpl();
            audioChannel.setSpeakerNumber(this.udaSpeaker.getDsSpeaker().getSpeakerNumber());

            final SeizeResourceResult result = this.svcCallControl.seizeResource(userAOR,
                    resourceAOR, appear, false, AudioDeviceType.SPEAKER, audioChannel);
            final ResultStatusType resStatusType = result.getResultStatus();
            if (resStatusType.equals(ResultStatusType.FAILURE))
            {
                fsm.ctiFailed();

                CenterlineMessageEvent centerlineMessageEvent = new CenterlineMessageEvent();
                centerlineMessageEvent.setMessage("What Failure Message have to send ? ");
                // TODO what message to pass to UDAC?
            }
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.SpeakerMockableActions#ctiSetCallTalkBack(boolean)
     */
    @Override
    public void ctiSetCallTalkBack(boolean isCallTalkbackEnabled)
    {
        try
        {
            this.svcCallControl.setCallTalkback(this.connectionID, isCallTalkbackEnabled);
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.callproc.SpeakerMockableActions#initiateCallOnSeize()
     */
    @Override
    public boolean initiateCallOnSeize()
    {
        return this.initiateCallOnSeize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ipc.uda.service.callproc.SpeakerMockableActions#udacSpeakerChannelActivityUpdated(int)
     */
    @Override
    public void udacSpeakerChannelActivityUpdated(int level)
    {
        SpeakerChannelActivityUpdatedEvent spkChnlActivityUpdatedEvent = new SpeakerChannelActivityUpdatedEvent();
        spkChnlActivityUpdatedEvent.setSpeakerNumber(this.udaSpeaker.getDsSpeaker()
                .getSpeakerNumber());
        spkChnlActivityUpdatedEvent.setLevel(level);

        final Event event = new Event();
        event.setSpeakerChannelActivityUpdated(spkChnlActivityUpdatedEvent);

        ExecutableResultQueue.<Event> send(event, ctx().getUser(), ctx().getUserID().getDeviceID());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ipc.uda.service.callproc.SpeakerMockableActions#udacSpeakerChannelStatusUpdated(com.ipc
     * .uda.types.EnumSpeakerChannelStatusType)
     */
    @Override
    public void udacSpeakerChannelStatusUpdated(EnumSpeakerChannelStatusType status)
    {
        final SpeakerChannelStatusUpdatedEvent spkChnlStatusUpdatedEvent = new SpeakerChannelStatusUpdatedEvent();
        spkChnlStatusUpdatedEvent.setSpeakerNumber(this.udaSpeaker.getDsSpeaker()
                .getSpeakerNumber());
        spkChnlStatusUpdatedEvent.setStatus(status);

        final Event event = new Event();
        event.setSpeakerChannelStatusUpdated(spkChnlStatusUpdatedEvent);

        ExecutableResultQueue.<Event> send(event, ctx().getUser(), ctx().getUserID().getDeviceID());
    }

    @Override
    public void udacSpeakerChannelPttFailed()
    {
        // TODO if VA cannot deliver us a PTT failed event, then we could remove this action
        SpeakerChannelPttFailedEvent SpkrChnlPttFailedEvent = new SpeakerChannelPttFailedEvent();
        SpkrChnlPttFailedEvent.setSpeakerNumber(this.udaSpeaker.getDsSpeaker().getSpeakerNumber());

        final Event event = new Event();
        event.setSpeakerChannelPttFailed(SpkrChnlPttFailedEvent);

        ExecutableResultQueue.<Event> send(event, ctx().getUser(), ctx().getUserID().getDeviceID());
    }
    
    @Override
    public void udacCliUpdate(String cliName, String cliNumber)
    {
        //TODO once xsd update for the new element speakerNumber in cliUpdatedEvent, have to send cliUpdatedEvent to UDAC
        try
        {
            final CliUpdatedEvent cliUpdateEvent = new CliUpdatedEvent();
            //cliUpdateEvent.setSpeakerNumber(this.udaSpeaker.getId());
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
      //TODO once xsd update for the new element speakerNumber in cliUpdatedEvent, have to send cpiUpdatedEvent to UDAC
        try
        {
            final CpiUpdatedEvent cpiUpdateEvent = new CpiUpdatedEvent();
            //cpiUpdateEvent.setSpeakerNumber(this.udaSpeaker.getId());
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

    private String getCalledParty()
    {
        if (this.digits == null || this.digits.isEmpty())
        {
            return this.appearance.getAor();
        }
        return this.digits;
    }

    @Override
    public void setActiveStatus(final boolean active)
    {
        try
        {
            final UserSpeakerChannel dsSpeaker = this.udaSpeaker.getDsSpeaker();
            if (dsSpeaker.getActiveStatus() != active)
            {
                dsSpeaker.setActiveStatus(active);

                final UserSpeakerChannelManager usrSprChlMgr = new UserSpeakerChannelManager(ctx()
                        .getSecurityContext());
                usrSprChlMgr.save(dsSpeaker);
            }
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }

    @Override
    public boolean isDialTone()
    {
        return this.lineDialOrPrivate.equals(ResourceAOR.EnumType.Dialtone);
    }

    @Override
    public void setVolume(final int volume)
    {
        try
        {
            final UserSpeakerChannel usrSpkrChannel = this.udaSpeaker.getDsSpeaker();
            if (usrSpkrChannel.getVolume() != volume)
            {
                usrSpkrChannel.setVolume(volume);

                final UserSpeakerChannelManager usrSprChlMgr = new UserSpeakerChannelManager(ctx()
                        .getSecurityContext());
                usrSprChlMgr.save(usrSpkrChannel);
            }
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }
}
