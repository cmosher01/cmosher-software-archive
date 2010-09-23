/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;


import javax.naming.NamingException;

import com.ipc.uda.service.util.UdaEnvironmentUtil;
import com.ipc.uda.service.util.smc.Actions;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.SystemLineStatus;
import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.callControl.events.CallControlOfferedEvent;
import commonj.timers.Timer;
import commonj.timers.TimerListener;

/**
 * @author mosherc
 */
public class SpeakerNonMockableActions
{
    private static final int RETRY_INTERVAL_IN_SECONDS = 10;
    protected final ButtonAppearance appearance;
    protected final UdaSpeaker udaSpeaker;
    protected String digits;
    protected ConnectionID connectionID;
    protected SystemLineStatus status = SystemLineStatus.IDLE;
    
    
    public SpeakerNonMockableActions(final ButtonAppearance appearance, final UdaSpeaker udaSpeaker)
    {
        this.appearance = appearance;
        this.udaSpeaker = udaSpeaker;
        if (this.appearance == null && this.udaSpeaker == null)
        {
            throw new IllegalArgumentException("AOR/Appearance/UdaSpeaker cannot be null");
        }
    }
    
    void setOfferedConnectionID(final CallControlOfferedEvent offered)
    {
        this.connectionID = offered.getConnectionID();
    }

    // an identifying name, for use by the log file
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(this.appearance);

        return sb.toString();
    }

    public void setDigits(final String digits)
    {
        this.digits = digits;
    }

    public void setLineStatus(final SystemLineStatus status)
    {
       this.status = status;
        
    }

    public SystemLineStatus getLineStatus()
    {
      
        return this.status;
    }

    public void startRetryTimer(final SpeakerFsmContext fsm)
    {
        // start a timer to call UDAC Press Speaker Channel
        UdaEnvironmentUtil.getTimerManager().schedule(new TimerListener()
        {
            @Override
            public void timerExpired(final Timer timer)
            {
                fsm.udacPressSpeakerChannel();
            }
        },SpeakerNonMockableActions.RETRY_INTERVAL_IN_SECONDS*1000);
    }

    public boolean isInGroup(final int group)
    {
        switch (group)
        {
            case 1:
                return this.udaSpeaker.getDsSpeaker().getIsInGroup1();
            case 2:
                return this.udaSpeaker.getDsSpeaker().getIsInGroup2();
            default:
                return false;
        }
    }
}
