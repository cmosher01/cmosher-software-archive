/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;



import com.ipc.uda.types.LineStatusType;
import com.ipc.va.cti.callControl.ConferenceActionType;
import com.ipc.va.cti.callControl.events.CallControlConferencedEvent;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;



/**
 * @author mosherc
 */
public interface PrivateWireMockableActions
{
    void udacLineStatusUpdated(LineStatusType status);
    void udacBarge();
    void udacBarged(CallControlConferencedEvent event);
    void udacCliUpdate(String name, String number);
    void udacCpiUpdate(CallControlInformationEvent event);

    void ctiMakeCall();
    void ctiConference(ConferenceActionType action);
    void ctiSeizeResource(PrivateWireFsmContext fsm);
    void ctiReleaseResource();
    void ctiCancelTelephonyTones();
    void ctiDialDigits(String digits);
    
    void putOntoHandset();
    void removeFromHandset();

    boolean initiateCallOnSeize();
    boolean isConference();
}
