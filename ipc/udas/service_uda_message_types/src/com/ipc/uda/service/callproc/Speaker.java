package com.ipc.uda.service.callproc;

import com.ipc.uda.service.util.smc.DefaultActions;
import com.ipc.uda.types.EnumSpeakerChannelStatusType;
import com.ipc.uda.types.SystemLineStatus;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;

/**
 * @author sharmar
 * @author mosherc
 */

public class Speaker extends DefaultActions implements SpeakerMockableActions
{
    private final SpeakerNonMockableActions delegateNonMockable;
    private final SpeakerMockableActions delegateMockable;

    public Speaker(
        final SpeakerNonMockableActions delegateNonMockable,
        final SpeakerMockableActions delegateMockable)
    {
        this.delegateMockable = delegateMockable;
        this.delegateNonMockable = delegateNonMockable;

        if (this.delegateMockable == null || this.delegateNonMockable == null)
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toString()
    {
        return this.delegateNonMockable.toString();
    }

    @Override
    public void ctiCancelTelephonyTones()
    {
        this.delegateMockable.ctiCancelTelephonyTones();
    }

    @Override
    public void ctiClearConnection(final EventCause cause)
    {
        this.delegateMockable.ctiClearConnection(cause);
    }

    @Override
    public void ctiDialDigits(String digits)
    {
        this.delegateMockable.ctiDialDigits(digits);
    }

    @Override
    public void ctiMakeCall()
    {
        this.delegateMockable.ctiMakeCall();
    }

    @Override
    public void ctiReleaseResource()
    {
        this.delegateMockable.ctiReleaseResource();
    }

    @Override
    public void ctiSeizeResource(final SpeakerFsmContext fsm)
    {
        this.delegateMockable.ctiSeizeResource(fsm);
    }

    @Override
    public void ctiSetCallTalkBack(boolean b)
    {
        this.delegateMockable.ctiSetCallTalkBack(b);
    }

    @Override
    public void ctiSetSpeakerChannelVolume(int volume)
    {
        this.delegateMockable.ctiSetSpeakerChannelVolume(volume);
    }

    @Override
    public boolean initiateCallOnSeize()
    {
        return delegateMockable.initiateCallOnSeize();
    }

    @Override
    public void udacSpeakerChannelActivityUpdated(int level)
    {
        this.delegateMockable.udacSpeakerChannelActivityUpdated(level);
    }

    @Override
    public void udacSpeakerChannelStatusUpdated(EnumSpeakerChannelStatusType off)
    {
        this.delegateMockable.udacSpeakerChannelStatusUpdated(off);
    }

    @Override
    public void udacSpeakerChannelVolumeUpdated(int volume)
    {
        this.delegateMockable.udacSpeakerChannelVolumeUpdated(volume);
    }

    @Override
    public void udacSpeakerChannelPttFailed()
    {
        this.delegateMockable.udacSpeakerChannelPttFailed();
    }
    
    @Override
    public void udacCliUpdate(String name, String number)
    {
        this.delegateMockable.udacCliUpdate(name,number);
    }
    
    @Override
    public void udacCpiUpdate(CallControlInformationEvent event)
    {
        this.delegateMockable.udacCpiUpdate(event);
    }
    
    public void setDigits(String digits)
    {
        this.delegateNonMockable.setDigits(digits);
    }
    
    public void setLineStatus(SystemLineStatus status)
    {
        this.delegateNonMockable.setLineStatus(status);  
    }

    public void setActiveStatus(boolean active)
    {
        this.delegateMockable.setActiveStatus(active);
    }
    
    public void setVolume(int volume)
    {
        this.delegateMockable.setVolume(volume);
    }
    
    public SystemLineStatus getLineStatus()
    {
        return delegateNonMockable.getLineStatus();
    }

    public boolean isDialTone()
    {
        return delegateMockable.isDialTone();
    }

    public void startRetryTimer(final SpeakerFsmContext fsm)
    {
        this.delegateNonMockable.startRetryTimer(fsm);
    }

    public boolean isInGroup(final int group)
    {
        return this.delegateNonMockable.isInGroup(group);
    }
}
