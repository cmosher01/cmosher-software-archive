/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ipc.uda.service.util.smc.monitor.StateChangeMonitor;
import com.ipc.uda.types.LineStatusType;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.callControl.events.CallControlDeliveredEvent;
import com.ipc.va.cti.callControl.events.CallControlOfferedEvent;

/**
 * @author mosherc
 */
@SuppressWarnings({"synthetic-access","unqualified-field-access"})
@RunWith(JMock.class)
public class IcmP2PTest
{
    private final Mockery mockery = new JUnit4Mockery();
    private IcmP2PMockableActions mock;
    private IcmP2PNonMockableActions nonmock;
    private IcmP2P actions;
    private IcmP2PFsmContext fsm;

    private static final String AOR = UUID.randomUUID().toString();
    private static final DeviceID deviceAOR = new DeviceID(){ public String getDeviceID() { return AOR; }};

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp()
    {
        this.mock = this.mockery.mock(IcmP2PMockableActions.class);
        this.nonmock = new IcmP2PNonMockableActions();
        this.actions = new IcmP2P(this.nonmock,this.mock);
        this.actions.setStateChangeMonitor(this.mockery.mock(StateChangeMonitor.class));
        this.fsm = new IcmP2PFsmContext(this.actions);
    }

    /**
     * Test method for {@link com.ipc.uda.service.callcontrol.IcmP2PImpl#UdaAcceptAlways()}.
     */
    @Test
    public void initial()
    {
        assertThat(IcmP2PFsmContext.IcmP2PFsm.Null,equalTo(this.fsm.getState()));
    }

    /**
     * If call is offered, then we automatically accept it, so it goes to ringing state
     */
    @Test
    public void incomingRing()
    {
        final Sequence seq = this.mockery.sequence("incomingRing");

        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).ctiAccept(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING); inSequence(seq);
            never (mock).ctiAnswer(); inSequence(seq);
        }});
        final CallControlOfferedEvent e = this.mockery.mock(CallControlOfferedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            oneOf (e).getCallingDeviceID().getDeviceID(); will(returnValue(deviceAOR));
            ignoring (e).getConnectionID();
        }});
        this.fsm.ctiOffered(e);
        assertEquals(AOR,this.nonmock.aor);

        assertThat(IcmP2PFsmContext.IcmP2PIncomingFsm.Ringing,equalTo(this.fsm.getState()));
    }

    /**
     * If we answer a ringing call, line goes I_BUSY and call is established.
     */
    @Test
    public void answerRinging()
    {
        final Sequence seq = this.mockery.sequence("answerRinging");

        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).ctiAccept(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING); inSequence(seq);
        }});
        final CallControlOfferedEvent e = this.mockery.mock(CallControlOfferedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            oneOf (e).getCallingDeviceID().getDeviceID(); will(returnValue(deviceAOR));
            ignoring (e).getConnectionID();
        }});
        this.fsm.ctiOffered(e);
        assertEquals(AOR,this.nonmock.aor);


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY); inSequence(seq);
            oneOf (mock).ctiAnswer(); inSequence(seq);
        }});
        this.fsm.udacIcmAnswer();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).putOntoHandset(); inSequence(seq);
            ignoring (mock).udacLineStatusUpdated(LineStatusType.I_BUSY); inSequence(seq);
        }});
        this.fsm.ctiEstablished();
        assertEquals(AOR,this.nonmock.aor);


        assertThat(IcmP2PFsmContext.IcmP2PFsm.Connected,equalTo(this.fsm.getState()));
    }

    /**
     * Release an (incoming) call, line goes IDLE and connection is cleared
     */
    @Test
    public void iRelease()
    {
        final Sequence seq = this.mockery.sequence("iRelease");

        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).ctiAccept(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING); inSequence(seq);
        }});
        final CallControlOfferedEvent e = this.mockery.mock(CallControlOfferedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            oneOf (e).getCallingDeviceID().getDeviceID(); will(returnValue(deviceAOR));
            ignoring (e).getConnectionID();
        }});
        this.fsm.ctiOffered(e);


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY); inSequence(seq);
            oneOf (mock).ctiAnswer(); inSequence(seq);
        }});
        this.fsm.udacIcmAnswer();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).putOntoHandset(); inSequence(seq);
            ignoring (mock).udacLineStatusUpdated(LineStatusType.I_BUSY); inSequence(seq);
        }});
        this.fsm.ctiEstablished();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).ctiClearConnection(EventCause.NORMAL_CLEARING); inSequence(seq);
            oneOf (mock).removeFromHandset(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE); inSequence(seq);
            oneOf (mock).destroy(); inSequence(seq);
        }});
        this.fsm.udacReleaseCall();


        assertEquals(AOR,this.nonmock.aor);
        assertThat(IcmP2PFsmContext.IcmP2PFsm.NullEnd,equalTo(this.fsm.getState()));
    }

    /**
     * If far end releases, line goes IDLE and connection is cleared.
     */
    @Test
    public void youRelease()
    {
        final Sequence seq = this.mockery.sequence("iRelease");

        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).ctiAccept(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING); inSequence(seq);
        }});
        final CallControlOfferedEvent e = this.mockery.mock(CallControlOfferedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            ignoring (e);
        }});
        this.fsm.ctiOffered(e);


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY); inSequence(seq);
            oneOf (mock).ctiAnswer(); inSequence(seq);
        }});
        this.fsm.udacIcmAnswer();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).putOntoHandset(); inSequence(seq);
            ignoring (mock).udacLineStatusUpdated(LineStatusType.I_BUSY); inSequence(seq);
        }});
        this.fsm.ctiEstablished();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).removeFromHandset(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE); inSequence(seq);
            oneOf (mock).destroy(); inSequence(seq);
        }});
        this.fsm.ctiConnectionCleared();


        assertThat(IcmP2PFsmContext.IcmP2PFsm.NullEnd,equalTo(this.fsm.getState()));
    }

    @Test
    public void outgoing()
    {
        final Sequence seq = this.mockery.sequence("outgoing");

        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).putOntoHandset(); inSequence(seq);
            oneOf (mock).ctiMakeCall(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY); inSequence(seq);
        }});
        this.fsm.udacIcmOutgoing(AOR);
        assertEquals(AOR,this.nonmock.aor);


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY); inSequence(seq);
        }});
        
        final CallControlDeliveredEvent delivered = this.mockery.mock(CallControlDeliveredEvent.class);
        this.mockery.checking(new Expectations()
        {{
            ignoring (delivered);
        }});
        this.fsm.ctiDelivered(delivered);


        this.mockery.checking(new Expectations()
        {{
            ignoring (mock).udacLineStatusUpdated(LineStatusType.I_BUSY); inSequence(seq);
        }});
        this.fsm.ctiEstablished();


        assertEquals(AOR,this.nonmock.aor);
        assertThat(IcmP2PFsmContext.IcmP2PFsm.Connected,equalTo(this.fsm.getState()));
    }

    @Test
    public void outgoingUnavailable()
    {
        final Sequence seq = this.mockery.sequence("outgoing");

        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).putOntoHandset(); inSequence(seq);
            oneOf (mock).ctiMakeCall(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY); inSequence(seq);
        }});
        this.fsm.udacIcmOutgoing(AOR);


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).removeFromHandset(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE); inSequence(seq);
            oneOf (mock).destroy(); inSequence(seq);
        }});
        this.fsm.ctiFailed();


        assertThat(IcmP2PFsmContext.IcmP2PFsm.NullEnd,equalTo(this.fsm.getState()));
    }

    /**
     * If we don't answer a ringing call, and the far end releases, we go idle.
     */
    @Test
    public void incomingMissed()
    {
        final Sequence seq = this.mockery.sequence("incomingMissed");

        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).ctiAccept(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.HI_RING); inSequence(seq);
        }});
        final CallControlOfferedEvent e = this.mockery.mock(CallControlOfferedEvent.class);
        this.mockery.checking(new Expectations()
        {{
            ignoring (e);
        }});
        this.fsm.ctiOffered(e);


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE); inSequence(seq);
            oneOf (mock).destroy(); inSequence(seq);
        }});
        this.fsm.ctiConnectionCleared();


        assertThat(IcmP2PFsmContext.IcmP2PFsm.NullEnd,equalTo(this.fsm.getState()));
    }

    /**
     * Release a ringing outgoing call, connection is dropped
     */
    @Test
    public void iDropRinging()
    {
        final Sequence seq = this.mockery.sequence("iDropRinging");

        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).putOntoHandset(); inSequence(seq);
            oneOf (mock).ctiMakeCall(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY); inSequence(seq);
        }});
        this.fsm.udacIcmOutgoing(AOR);


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY); inSequence(seq);
        }});
        final CallControlDeliveredEvent delivered = this.mockery.mock(CallControlDeliveredEvent.class);
        this.mockery.checking(new Expectations()
        {{
            ignoring (delivered);
        }});
        this.fsm.ctiDelivered(delivered);


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).ctiClearConnection(EventCause.CALL_CANCELLED); inSequence(seq);
            oneOf (mock).removeFromHandset(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.IDLE); inSequence(seq);
            oneOf (mock).destroy(); inSequence(seq);
        }});
        this.fsm.udacReleaseCall();


        assertThat(IcmP2PFsmContext.IcmP2PFsm.NullEnd,equalTo(this.fsm.getState()));
    }

    /**
     * An outgoing ICM call that is auto-answered at the far end (that is, it doesn't
     * ring, so we don't get a Delivered event from CTI).
     */
    @Test
    public void outgoingAutoAnswered()
    {
        final Sequence seq = this.mockery.sequence("outgoingAutoAnswered");

        this.fsm.enterStartState();


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).putOntoHandset(); inSequence(seq);
            oneOf (mock).ctiMakeCall(); inSequence(seq);
            oneOf (mock).udacLineStatusUpdated(LineStatusType.U_BUSY); inSequence(seq);
        }});
        this.fsm.udacIcmOutgoing(AOR);


        this.mockery.checking(new Expectations()
        {{
            oneOf (mock).udacLineStatusUpdated(LineStatusType.I_BUSY); inSequence(seq);
        }});
        this.fsm.ctiEstablished();


        assertThat(IcmP2PFsmContext.IcmP2PFsm.Connected,equalTo(this.fsm.getState()));
    }
}
