package com.ipc.uda.service.callproc;

import com.ipc.uda.service.util.smc.DefaultActions;
import com.ipc.uda.types.LineStatusType;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;
import com.ipc.va.cti.callControl.events.CallControlOfferedEvent;

/**
 * Action delegate for IcmP2P FSM. This class contains all the actions called by
 * the IcmP2P finite state machine (FSM). The IcmP2P FSM is defined in the
 * IcmP2P.sm file (smc.sf.net source code) (and IcmP2PFsmContext.java, generated
 * java code from the IcmP2P.sm file by smc.sf.net compiler).
 * This class is just a delegate. It delegates all actions to one of its two
 * delegates. The actions are split into two categories, mockable and non-mockable.
 * Mockable actions are actions that can be mocked (using JMock) during unit testing,
 * and implemented to do actual work in production. They are actions that we
 * do not want the unit test to actually test. Non-mockable actions, on the other
 * hand, are actions that we do want the unit test to test, and therefore must be
 * implemented for use in both unit tests and production code.
 * 
 * @author mosherc
 */
public class IcmP2P extends DefaultActions implements IcmP2PMockableActions
{
    private final IcmP2PNonMockableActions delegateNonMockable;
    private final IcmP2PMockableActions delegateMockable;

    public IcmP2P(final IcmP2PNonMockableActions delegateNonMockable, final IcmP2PMockableActions delegateMockable)
    {
        this.delegateNonMockable = delegateNonMockable;
        this.delegateMockable = delegateMockable;

        if (this.delegateMockable == null || this.delegateNonMockable == null)
        {
            throw new IllegalStateException();
        }
    }



    // mockable delegate methods

    @Override
    public void udacLineStatusUpdated(final LineStatusType status)
    {
        this.delegateMockable.udacLineStatusUpdated(status);
    }

    @Override
    public void ctiAccept()
    {
        this.delegateMockable.ctiAccept();
    }

    @Override
    public void ctiAnswer()
    {
        this.delegateMockable.ctiAnswer();
    }

    @Override
    public void ctiClearConnection(final EventCause cause)
    {
        this.delegateMockable.ctiClearConnection(cause);
    }

    @Override
    public void ctiMakeCall()
    {
        this.delegateMockable.ctiMakeCall();
    }

    @Override
    public void putOntoHandset()
    {
        this.delegateMockable.putOntoHandset();
    }

    @Override
    public void removeFromHandset()
    {
        this.delegateMockable.removeFromHandset();
    }

    @Override
    public void destroy()
    {
        this.delegateMockable.destroy();
    }

    @Override
    public void udacCpiUpdate(CallControlInformationEvent event)
    {
        this.delegateMockable.udacCpiUpdate(event);
    }













    // non-mockable delegate methods

    @Override
    public String toString()
    {
        return this.delegateNonMockable.toString();
    }

    void setAor(String aor)
    {
        this.delegateNonMockable.setAor(aor);
    }

    void setOfferedAor(final CallControlOfferedEvent offered)
    {
        this.delegateNonMockable.setOfferedAor(offered);
    }


    void autoUdaAccept(final IcmP2PFsmContext context)
    {
        this.delegateNonMockable.autoUdaAccept(context);
    }

    void autoUdaNull(final IcmP2PFsmContext context)
    {
        this.delegateNonMockable.autoUdaNull(context);
    }
}
