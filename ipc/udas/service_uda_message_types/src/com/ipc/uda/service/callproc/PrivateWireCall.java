/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;

import com.ipc.uda.service.callproc.PrivateWireFsmContext.PrivateWireState;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.util.smc.monitor.StateChangeMonitorFull;
import com.ipc.uda.service.util.smc.monitor.listener.StateChangeLogger;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.UID;
import com.ipc.va.cti.callControl.events.CallControlConferencedEvent;
import com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent;
import com.ipc.va.cti.callControl.events.CallControlDeliveredEvent;
import com.ipc.va.cti.callControl.events.CallControlEstablishedEvent;
import com.ipc.va.cti.callControl.events.CallControlFailedEvent;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;
import com.ipc.va.cti.callControl.events.CallControlOfferedEvent;



/**
 * @author mosherc
 */
public class PrivateWireCall implements ButtonPressCall
{
    private final PrivateWireFsmContext fsm;
    private final UserContext ctx;

    @SuppressWarnings("unused") private StateChangeMonitorFull mon;

    public PrivateWireCall(
        final UserContext ctx,
        final ButtonAppearance appearance,
        final UdaButton udaButton,
        final boolean initiateCallOnSeize)
    {
        this.ctx = ctx;
        if (this.ctx == null)
        {
            throw new IllegalStateException("UserContext cannot be null");
        }
        
        final PrivateWireActions actions = new PrivateWireActions(this, appearance, udaButton, initiateCallOnSeize);
        this.fsm = new PrivateWireFsmContext(new PrivateWire(actions, actions));
    }
    
    public void startFSM()
    {
        this.mon = new StateChangeMonitorFull(this.fsm,new StateChangeLogger(this.fsm.getOwner()));
    }

    UserContext getContext()
    {
        return this.ctx;
    }



    @Override
    public void release()
    {
        this.fsm.udacReleaseCall();
    }

    @Override
    public void ctiDelivered(final CallControlDeliveredEvent event)
    {
        // shouldn't happen
    }

    @Override
    public void ctiEstablished(final CallControlEstablishedEvent event)
    {
        this.fsm.ctiEstablished();
    }

    @Override
    public void ctiOffered(final CallControlOfferedEvent event)
    {
        // shouldn't happen
    }

    @Override
    public void ctiBarged(CallControlConferencedEvent event)
    {
        this.fsm.ctiBarged(event);
    }

    @Override
    public void udacHold()
    {
        this.fsm.udacHold();
    }

    @Override
    public void udacButtonPressed()
    {
        this.fsm.udacButtonPressed();
    }

    @Override
    public void ctiConnectionCleared(final CallControlConnectionClearedEvent event)
    {
        this.fsm.ctiConnectionCleared(event);
    }

    @Override
    public void ctiFailed(final CallControlFailedEvent event)
    {
        this.fsm.ctiFailed();
    }

    @Override
    public void lineBusy()
    {
        this.fsm.lineBusy();
    }

    @Override
    public void lineHold()
    {
        this.fsm.lineHold();
    }

    @Override
    public void lineIdle()
    {
        this.fsm.lineIdle();
    }

    @Override
    public void lineRing()
    {
        this.fsm.lineRing();
    }
    
    @Override
    public PrivateWireState getState()
    {
        return this.fsm.getState();
    }

    @Override
    public void lineCli(String name, String number)
    {
        this.fsm.lineCli(name,number);
    }

    @Override
    public void ctiCallInformation(CallControlInformationEvent event)
    {
        this.fsm.ctiCallInformation(event);
    }

    @Override
    public void udacDigitsDialed(String digits)
    {
        this.fsm.udacDigitsDialed(digits);
    }

    @Override
    public void ctiConferenced(CallControlConferencedEvent event)
    {
        this.fsm.ctiConferenced(event);
    }

    @Override
    public boolean canConference()
    {
        return true;
    }
}
