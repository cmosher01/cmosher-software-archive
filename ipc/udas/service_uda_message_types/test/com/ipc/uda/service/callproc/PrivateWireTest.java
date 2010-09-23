/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.internal.dto.impl.ButtonImpl;
import com.ipc.uda.service.util.smc.monitor.StateChangeMonitor;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.LineStatusType;
import com.ipc.uda.types.UID;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.callControl.events.CallControlConnectionClearedEvent;

/**
 * @author mosherc
 */
@SuppressWarnings({"synthetic-access","unqualified-field-access"})
@RunWith(JMock.class)
public class PrivateWireTest
{
    private final Mockery mockery = new JUnit4Mockery();
    private PrivateWireMockableActions mock;
    private PrivateWire actions;
    private PrivateWireFsmContext fsm;

    /**
     * @throws ButtonAppearance.Invalid
     */
    @Before
    public void setUp() throws ButtonAppearance.Invalid
    {
        this.mock = this.mockery.mock(PrivateWireMockableActions.class);
        final ButtonAppearance appearance = new ButtonAppearance("PLIX 5",1);
        
        final Button dsButton = new ButtonImpl();
        final CallBasedButton callBasedButton = new CallBasedButton(dsButton, appearance);
        
        this.actions = new PrivateWire(new PrivateWireNonMockableActions(appearance,callBasedButton),this.mock);
        
        this.actions.setStateChangeMonitor(this.mockery.mock(StateChangeMonitor.class));
        this.fsm = new PrivateWireFsmContext(this.actions);
    }

    /**
     * Test that initial state is Null (IDLE). 
     */
    @Test
    public void initial()
    {
        assertThat(PrivateWireFsmContext.PrivateWireFsm.Null,equalTo(this.fsm.getState()));
    }

    /**
     * Test that line going busy causes U_BUSY.
     */
    @Test
    public void uBusyOutgoing()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY);
        }});
        this.fsm.lineBusy();
        this.fsm.lineBusy(); // more BUSYs have no effect


        assertThat(PrivateWireFsmContext.PrivateWireFsm.UBusy,equalTo(this.fsm.getState()));
    }

    /**
     * Test that line going busy then idle causes U_BUSY then IDLE.
     */
    @Test
    public void uBusyToIdle()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY);
        }});
        this.fsm.lineBusy();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.lineIdle();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.Null,equalTo(this.fsm.getState()));
    }

    /**
     * Test incoming ring
     */
    @Test
    public void ring()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.Ringing,equalTo(this.fsm.getState()));
    }

    /**
     * Test incoming U_BUSY
     */
    @Test
    public void uBusyIncoming()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY);
        }});
        this.fsm.lineBusy();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.UBusy,equalTo(this.fsm.getState()));
    }

    /**
     * Test U_HOLD
     */
    @Test
    public void uHold()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY);
        }});
        this.fsm.lineBusy();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_HOLD);
        }});
        this.fsm.lineHold();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.UHold,equalTo(this.fsm.getState()));
    }


    /**
     * Test answer incoming, established followed by BUSY.
     */
    @Test
    public void answerEstablishedBusy()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).putOntoHandset();
        }});
        this.fsm.ctiEstablished();
        this.fsm.lineBusy();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.IBusy,equalTo(this.fsm.getState()));
    }

    /**
     * Test answer incoming, BUSY followed by established.
     */
    @Test
    public void answerBusyEstablished()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).putOntoHandset();
        }});
        this.fsm.lineBusy();
        this.fsm.ctiEstablished();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.IBusy,equalTo(this.fsm.getState()));
    }

    /**
     * Test seize.
     */
    @Test
    public void seize()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).initiateCallOnSeize(); will(returnValue(true));
        }});
        this.fsm.udacButtonPressed();
    }

    /**
     * Test seize failed
     */
    @Test
    public void seizeFailed()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).initiateCallOnSeize(); will(returnValue(true));
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.ctiFailed();
        this.fsm.udacReleaseCall();

        assertThat(PrivateWireFsmContext.PrivateWireFsm.Null,equalTo(this.fsm.getState()));
    }

    /**
     * Test missed
     */
    @Test
    public void missed()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.lineIdle();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.Null,equalTo(this.fsm.getState()));
    }

    /**
     * Test release by far end
     */
    @Test
    public void farEndRelease()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).putOntoHandset();
        }});
        this.fsm.lineBusy();
        this.fsm.ctiEstablished();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).removeFromHandset();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.lineIdle();
        final CallControlConnectionClearedEvent clear = this.mockery.mock(CallControlConnectionClearedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            ignoring (clear);
        }});
        this.fsm.ctiConnectionCleared(clear);


        assertThat(PrivateWireFsmContext.PrivateWireFsm.Null,equalTo(this.fsm.getState()));
    }

    /**
     * Test barge.
     */
    @Test
    public void barge()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY);
        }});
        this.fsm.lineBusy();


        this.mockery.checking(new Expectations()
        {{
            atLeast(1).of (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacBarge();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).putOntoHandset();
        }});
        this.fsm.ctiEstablished();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.IBusy,equalTo(this.fsm.getState()));
    }

    /**
     * Test barge failed.
     */
    @Test
    public void bargeFailed()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY);
        }});
        this.fsm.lineBusy();


        this.mockery.checking(new Expectations()
        {{
            atLeast(1).of (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY);
        }});
        this.fsm.ctiFailed();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.UBusy,equalTo(this.fsm.getState()));
    }

    /**
     * Test release
     */
    @Test
    public void release()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).ctiMakeCall();
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).putOntoHandset();
        }});
        this.fsm.lineBusy();
        this.fsm.ctiEstablished();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).removeFromHandset();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY);
        }});
        this.fsm.udacReleaseCall();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.UBusy,equalTo(this.fsm.getState()));
    }

    /**
     * Test I_HOLD
     */
    @Test
    public void iHoldWithClearedHold()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            atLeast(1).of (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            allowing (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).putOntoHandset();
        }});
        this.fsm.lineBusy();
        this.fsm.ctiEstablished();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).removeFromHandset();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_HOLD);
        }});
        this.fsm.udacHold();


        this.mockery.checking(new Expectations()
        {{
            allowing (mock).udacLineStatusUpdated(LineStatusType.I_HOLD);
        }});
        final CallControlConnectionClearedEvent clear = this.mockery.mock(CallControlConnectionClearedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            oneOf (clear).getEventCause(); will(returnValue(EventCause.HOLD_SUCCESS));
        }});
        this.fsm.ctiConnectionCleared(clear);
        this.fsm.lineHold();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.IHold,equalTo(this.fsm.getState()));
    }

    /**
     * Test I_HOLD
     */
    @Test
    public void iHoldWithHoldCleared()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            atLeast(1).of (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).putOntoHandset();
        }});
        this.fsm.lineBusy();
        this.fsm.ctiEstablished();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).removeFromHandset();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_HOLD);
        }});
        this.fsm.udacHold();


        this.mockery.checking(new Expectations()
        {{
            allowing (mock).udacLineStatusUpdated(LineStatusType.I_HOLD);
        }});
        final CallControlConnectionClearedEvent clear = this.mockery.mock(CallControlConnectionClearedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            oneOf (clear).getEventCause(); will(returnValue(EventCause.HOLD_SUCCESS));
        }});
        this.fsm.lineHold();
        this.fsm.ctiConnectionCleared(clear);


        assertThat(PrivateWireFsmContext.PrivateWireFsm.IHold,equalTo(this.fsm.getState()));
    }

    /**
     * Test hold failed
     */
    @Test
    public void iHoldFailed()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            atLeast(1).of (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).putOntoHandset();
        }});
        this.fsm.lineBusy();
        this.fsm.ctiEstablished();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).removeFromHandset();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_HOLD);
        }});
        this.fsm.udacHold();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY);
        }});
        final CallControlConnectionClearedEvent clear = this.mockery.mock(CallControlConnectionClearedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            oneOf (clear).getEventCause(); will(returnValue(EventCause.HOLD_DENIAL));
        }});
        this.fsm.ctiConnectionCleared(clear);


        assertThat(PrivateWireFsmContext.PrivateWireFsm.UBusy,equalTo(this.fsm.getState()));
    }

    /**
     * Test on-hold call, far end releases
     */
    @Test
    public void iHoldToIdle()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            atLeast(1).of (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).putOntoHandset();
        }});
        this.fsm.lineBusy();
        this.fsm.ctiEstablished();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).removeFromHandset();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_HOLD);
        }});
        this.fsm.udacHold();


        this.mockery.checking(new Expectations()
        {{
            allowing (mock).udacLineStatusUpdated(LineStatusType.I_HOLD);
        }});
        final CallControlConnectionClearedEvent clear = this.mockery.mock(CallControlConnectionClearedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            oneOf (clear).getEventCause(); will(returnValue(EventCause.HOLD_SUCCESS));
        }});
        this.fsm.ctiConnectionCleared(clear);
        this.fsm.lineHold();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.lineIdle();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.Null,equalTo(this.fsm.getState()));
    }

    /**
     * Test retrieve an I_HOLD call
     */
    @Test
    public void retrieve()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            atLeast(1).of (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).putOntoHandset();
        }});
        this.fsm.lineBusy();
        this.fsm.ctiEstablished();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).removeFromHandset();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_HOLD);
        }});
        this.fsm.udacHold();


        this.mockery.checking(new Expectations()
        {{
            allowing (mock).udacLineStatusUpdated(LineStatusType.I_HOLD);
        }});
        final CallControlConnectionClearedEvent clear = this.mockery.mock(CallControlConnectionClearedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            oneOf (clear).getEventCause(); will(returnValue(EventCause.HOLD_SUCCESS));
        }});
        this.fsm.ctiConnectionCleared(clear);
        this.fsm.lineHold();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();
    }

    /**
     * Test someone else retrieves an I_HOLD call (i.e., I_HOLD to U_BUSY)
     */
    @Test
    public void uRetrieveMine()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING);
        }});
        this.fsm.lineRing();


        this.mockery.checking(new Expectations()
        {{
            atLeast(1).of (mock).isConference();  will(returnValue(false)); 
            oneOf (mock).ctiMakeCall();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
        }});
        this.fsm.udacButtonPressed();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY);
            oneOf (mock).putOntoHandset();
        }});
        this.fsm.lineBusy();
        this.fsm.ctiEstablished();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).removeFromHandset();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_HOLD);
        }});
        this.fsm.udacHold();


        this.mockery.checking(new Expectations()
        {{
            allowing (mock).udacLineStatusUpdated(LineStatusType.I_HOLD);
        }});
        final CallControlConnectionClearedEvent clear = this.mockery.mock(CallControlConnectionClearedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            oneOf (clear).getEventCause(); will(returnValue(EventCause.HOLD_SUCCESS));
        }});
        this.fsm.ctiConnectionCleared(clear);
        this.fsm.lineHold();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY);
        }});
        this.fsm.lineBusy();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.UBusy,equalTo(this.fsm.getState()));
    }

    /**
     * Test that line in HOLD state upon UDAS startup is shown as U_HOLD
     */
    @Test
    public void uHoldUponStartup()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_HOLD);
        }});
        this.fsm.lineHold();


        assertThat(PrivateWireFsmContext.PrivateWireFsm.UHold,equalTo(this.fsm.getState()));
    }
}
