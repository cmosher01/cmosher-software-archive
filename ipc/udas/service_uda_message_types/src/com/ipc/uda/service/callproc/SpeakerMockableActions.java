package com.ipc.uda.service.callproc;

import com.ipc.uda.types.EnumSpeakerChannelStatusType;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;

/**
 * @author sharmar
 * @author mosherc
 */

public interface SpeakerMockableActions
{
    void ctiCancelTelephonyTones();
    void ctiClearConnection(EventCause cause);
    void ctiDialDigits(String digits);
    void ctiMakeCall();
    void ctiReleaseResource();
    void ctiSeizeResource(SpeakerFsmContext fsm);
    void ctiSetCallTalkBack(boolean b);
    void ctiSetSpeakerChannelVolume(int volume);
    
    boolean initiateCallOnSeize();
    
    void udacSpeakerChannelActivityUpdated(int level);
    void udacSpeakerChannelStatusUpdated(EnumSpeakerChannelStatusType off);
    void udacSpeakerChannelVolumeUpdated(int volume);
    void udacSpeakerChannelPttFailed();
    void udacCliUpdate(String name, String number);
    void udacCpiUpdate(CallControlInformationEvent event);
    
    void setActiveStatus(boolean active);
    void setVolume(int volume);
    boolean isDialTone();   
}
