/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;

import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.service.util.smc.monitor.StateChangeMonitorFull;
import com.ipc.uda.service.util.smc.monitor.listener.StateChangeLogger;
import com.ipc.uda.types.SystemLineStatus;
import com.ipc.va.cti.callControl.events.CallControlConferencedEvent;
import com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent;
import com.ipc.va.cti.callControl.events.CallControlDeliveredEvent;
import com.ipc.va.cti.callControl.events.CallControlEstablishedEvent;
import com.ipc.va.cti.callControl.events.CallControlFailedEvent;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;
import com.ipc.va.cti.callControl.events.CallControlOfferedEvent;

/**
 * @author mordarsd
 *
 */
public class IcmP2PCall implements Call
{
    private final IcmP2PFsmContext fsm;
    private final UserContext ctx;

    @SuppressWarnings("unused") private StateChangeMonitorFull mon;

    public IcmP2PCall(final UserContext ctx)
    {
        this.ctx = ctx;
        if (this.ctx == null)
        {
            throw new IllegalStateException("cannot be null");
        }
 
        final IcmP2PActions actions = new IcmP2PActions(this);
        this.fsm = new IcmP2PFsmContext(new IcmP2P(actions,actions));
    }

    public void startFSM()
    {
        this.mon = new StateChangeMonitorFull(this.fsm,new StateChangeLogger(this.fsm.getOwner()));
    }

    public UserContext getContext()
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
        this.fsm.ctiDelivered(event);
    }

    @Override
    public void ctiEstablished(final CallControlEstablishedEvent event)
    {
        this.fsm.ctiEstablished();
    }

    @Override
    public void ctiOffered(final CallControlOfferedEvent event)
    {
        this.fsm.ctiOffered(event);
    }

    /**
     * 
     */
    public void udacIcmAnswer()
    {
        this.fsm.udacIcmAnswer();
    }

    /**
     * @param userAor
     */
    public void udacIcmOutgoing(final String userAor)
    {
        this.fsm.udacIcmOutgoing(userAor);
    }

    @Override
    public void ctiConnectionCleared(final CallControlConnectionClearedEvent event)
    {
        this.fsm.ctiConnectionCleared();
    }

    @Override
    public void ctiFailed(final CallControlFailedEvent event)
    {
        this.fsm.ctiFailed();
    }

    @Override
    public void lineBusy()
    {
        Log.logger().info("Received unexpected line status "+SystemLineStatus.BUSY+" for ICM call.");
    }

    @Override
    public void lineHold()
    {
        Log.logger().info("Received unexpected line status "+SystemLineStatus.HOLD+" for ICM call.");
    }

    @Override
    public void lineIdle()
    {
        Log.logger().info("Received unexpected line status "+SystemLineStatus.IDLE+" for ICM call.");
    }

    @Override
    public void lineRing()
    {
        Log.logger().info("Received unexpected line status "+SystemLineStatus.INCOMING+" for ICM call.");
    }
    
    @Override
    public void udacHold()
    {
        Log.logger().info("Received unexpected hold command for ICM call.");
    }

    @Override
    public void ctiBarged(CallControlConferencedEvent event)
    {
        Log.logger().info("Received unexpected barged event for ICM call.");
    }

    @Override
    public void lineCli(String name, String number)
    {
        Log.logger().info("Received unexpected CLI event for ICM call: "+name+" "+number);
    }

    @Override
    public void ctiCallInformation(CallControlInformationEvent event)
    {
        this.fsm.ctiCallInformation(event);
    }

    @Override
    public void udacDigitsDialed(String digits)
    {
        Log.logger().info("Received unexpected digits dialed event for ICM call.");
    }

    @Override
    public void ctiConferenced(CallControlConferencedEvent event)
    {
        Log.logger().info("Received unexpected Conferenced event for ICM call.");
    }

    @Override
    public boolean canConference()
    {
        return false;
    }
}
