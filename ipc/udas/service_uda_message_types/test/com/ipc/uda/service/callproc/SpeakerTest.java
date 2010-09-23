/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.internal.dto.impl.UserSpeakerChannelImpl;
import com.ipc.uda.service.util.smc.monitor.StateChangeMonitor;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.EnumSpeakerChannelStatusType;
import com.ipc.va.cti.EventCause;

/**
 * @author mosherc
 */
@SuppressWarnings({"synthetic-access","unqualified-field-access"})
@RunWith(JMock.class)
public class SpeakerTest
{
    private final Mockery mockery = new JUnit4Mockery();
    private SpeakerMockableActions mock;
    private Speaker actions;
    private SpeakerFsmContext fsm;

    /**
     * Set up an FSM with mock actions.
     * @throws ButtonAppearance.Invalid
     */
    @Before
    public void setUp() throws ButtonAppearance.Invalid
    {
        this.mock = this.mockery.mock(SpeakerMockableActions.class);

        final ButtonAppearance appearance = new ButtonAppearance("PLIX 5",1);
        final UserSpeakerChannel dsSpeaker = new UserSpeakerChannelImpl();
        final UdaSpeaker udaSpeaker = new UdaSpeaker(dsSpeaker,appearance);

        this.actions = new Speaker(new SpeakerNonMockableActions(appearance,udaSpeaker),this.mock);
        this.actions.setStateChangeMonitor(this.mockery.mock(StateChangeMonitor.class));

        this.fsm = new SpeakerFsmContext(this.actions);
    }

    /**
     * Test that initial state is Off.
     */
    @Test
    public void initial()
    {
        assertThat(SpeakerFsmContext.SpeakerFsm.Off,equalTo(this.fsm.getState()));
    }

    /**
     * Test turning on a private wire speaker.
     */
    @Test
    public void turnOnPrivateWire()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).initiateCallOnSeize(); will(returnValue(true));
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).setActiveStatus(false);
            oneOf (mock).udacSpeakerChannelStatusUpdated(EnumSpeakerChannelStatusType.OFF);
        }});
        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacSpeakerChannelStatusUpdated(EnumSpeakerChannelStatusType.ON);
            oneOf (mock).ctiMakeCall();
            oneOf (mock).setActiveStatus(true);
        }});
        this.fsm.udacPressSpeakerChannel();

        this.fsm.ctiEstablished();

        assertThat(SpeakerFsmContext.SpeakerFsm.On,equalTo(this.fsm.getState()));
    }


    /**
     * Test turning off a private wire speaker.
     */
    @Test
    public void turnOffPrivateWire()
    {
        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).initiateCallOnSeize(); will(returnValue(true));
            ignoring (mock).ctiCancelTelephonyTones();
            ignoring (mock).ctiReleaseResource();
            oneOf (mock).setActiveStatus(false);
            oneOf (mock).udacSpeakerChannelStatusUpdated(EnumSpeakerChannelStatusType.OFF);
        }});
        this.fsm.enterStartState();


        // first turn it on
        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacSpeakerChannelStatusUpdated(EnumSpeakerChannelStatusType.ON);
            oneOf (mock).ctiMakeCall();
            oneOf (mock).setActiveStatus(true);
        }});
        this.fsm.udacPressSpeakerChannel();

        this.fsm.ctiEstablished();


        // now we can turn it off
        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacSpeakerChannelStatusUpdated(EnumSpeakerChannelStatusType.OFF);
            oneOf (mock).ctiClearConnection(EventCause.CALL_CANCELLED);
            oneOf (mock).setActiveStatus(false);
        }});
        this.fsm.udacReleaseSpeakerChannel();

        assertThat(SpeakerFsmContext.SpeakerFsm.Off,equalTo(this.fsm.getState()));
    }
}
