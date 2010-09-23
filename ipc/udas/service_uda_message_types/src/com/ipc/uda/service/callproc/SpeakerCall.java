package com.ipc.uda.service.callproc;


import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.util.smc.monitor.StateChangeMonitorFull;
import com.ipc.uda.service.util.smc.monitor.listener.StateChangeLogger;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;

/**
 * @author sharmar
 *
 */

public class SpeakerCall
{
    private final SpeakerFsmContext fsm;
    private final UdaSpeaker udaSpeaker;
    private final UserContext ctx;

    @SuppressWarnings("unused") private StateChangeMonitorFull mon;

    public SpeakerCall(
        final UserContext ctx,
        final ButtonAppearance appearance,
        final UdaSpeaker udaSpeaker,
        final boolean initiateCallOnSeize,
        final ResourceAOR.EnumType lineDialOrPrivate)
    {
        this.ctx = ctx;
        if (this.ctx == null)
        {
            throw new IllegalStateException("UserContext cannot be null");
        }
        this.udaSpeaker = udaSpeaker;
        
        final SpeakerActions actions = new SpeakerActions(this, appearance, udaSpeaker, initiateCallOnSeize, lineDialOrPrivate);
        this.fsm = new SpeakerFsmContext(new Speaker(actions, actions));
    }
    
    public void startFSM()
    {
        this.mon = new StateChangeMonitorFull(this.fsm,new StateChangeLogger(this.fsm.getOwner()));

        // if speaker was active (as indicated in the database) then automatically turn it on
        if (this.udaSpeaker.getDsSpeaker().getActiveStatus())
        {
            this.fsm.udacPressSpeakerChannel();
        }
    }

    UserContext getContext()
    {
        return this.ctx;
    }

    public void ctiConnectionCleared(final CallControlConnectionClearedEvent event)
    {
        this.fsm.ctiConnectionCleared(event);
    }

    public void ctiEstablished()
    {
        this.fsm.ctiEstablished();
    }

    public void ctiFailed()
    {
        this.fsm.ctiFailed();
    }

    public void ctiSpeakerActivity(final int level)
    {
        this.fsm.ctiSpeakerActivity(level);
    }
    
    public void ctiCallInformation(CallControlInformationEvent event)
    {
        this.fsm.ctiCallInformation(event);
    }

    public void lineBusy()
    {
        this.fsm.lineBusy();
    }

    public void lineIdle()
    {
        this.fsm.lineIdle();
    }
    
    public void lineRing()
    {
        this.fsm.lineRing();
    }
    
    public void lineHold()
    {
        this.fsm.lineHold();
    }

    public void lineCli(String cliName, String cliNumber)
    {
        this.fsm.lineCli(cliName, cliNumber);
    }
    
    public void udacDialDigits(final String digits)
    {
        this.fsm.udacDialDigits(digits);
    }

    public void udacPressPTT()
    {
        this.fsm.udacPressPTT();
    }

    public void udacPressSpeakerChannel()
    {
        this.fsm.udacPressSpeakerChannel();
    }

    public void udacReleasePTT()
    {
        this.fsm.udacReleasePTT();
    }

    public void udacReleaseSpeakerChannel()
    {
        this.fsm.udacReleaseSpeakerChannel();
    }

    public void udacSetSpeakerChannelVolume(final int volume)
    {
        this.fsm.udacSetSpeakerChannelVolume(volume);
    }

    public void udacPressGroupPTT(final int group)
    {
        this.fsm.udacPressGroupPTT(group);
    }

    public void udacReleaseGroupPTT(final int group)
    {
        this.fsm.udacReleaseGroupPTT(group);
    }
}
