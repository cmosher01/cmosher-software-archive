/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;

import com.ipc.va.cti.callControl.events.CallControlConferencedEvent;
import com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent;
import com.ipc.va.cti.callControl.events.CallControlDeliveredEvent;
import com.ipc.va.cti.callControl.events.CallControlEstablishedEvent;
import com.ipc.va.cti.callControl.events.CallControlFailedEvent;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;
import com.ipc.va.cti.callControl.events.CallControlOfferedEvent;

/**
 * @author mosherc
 *
 */
public interface Call
{
    void startFSM();

    void release();

    void ctiDelivered(CallControlDeliveredEvent event);
    void ctiEstablished(CallControlEstablishedEvent event);
    void ctiOffered(CallControlOfferedEvent event);
    void ctiConnectionCleared(CallControlConnectionClearedEvent event);
    void ctiFailed(CallControlFailedEvent event);
    void ctiBarged(CallControlConferencedEvent event);
    void ctiCallInformation(CallControlInformationEvent event);
    void ctiConferenced(CallControlConferencedEvent event);

    void lineIdle();
    void lineBusy();
    void lineRing();
    void lineHold();
    void lineCli(String name, String number);

    void udacHold();
    void udacDigitsDialed(String digits);

    boolean canConference();
}
