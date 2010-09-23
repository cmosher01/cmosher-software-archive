package com.ipc.uda.service.callproc;



import com.ipc.uda.service.util.smc.DefaultActions;
import com.ipc.uda.types.LineStatusType;
import com.ipc.va.cti.callControl.ConferenceActionType;
import com.ipc.va.cti.callControl.events.CallControlConferencedEvent;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;



public class PrivateWire extends DefaultActions implements PrivateWireMockableActions
{
    private final PrivateWireNonMockableActions delegateNonMockable;
    private final PrivateWireMockableActions delegateMockable;

    public PrivateWire(
        final PrivateWireNonMockableActions delegateNonMockable,
        final PrivateWireMockableActions delegateMockable)
    {
        this.delegateMockable = delegateMockable;
        this.delegateNonMockable = delegateNonMockable;

        if (this.delegateMockable == null || this.delegateNonMockable == null)
        {
            throw new IllegalStateException();
        }
    }

    // delegate methods

    @Override
    public String toString()
    {
        return this.delegateNonMockable.toString();
    }

    @Override
    public void udacLineStatusUpdated(final LineStatusType status)
    {
        this.delegateMockable.udacLineStatusUpdated(status);
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

    public boolean isConference()
    {
        return this.delegateMockable.isConference();
    }

    @Override
    public boolean initiateCallOnSeize()
    {
        return this.delegateMockable.initiateCallOnSeize();
    }

    public void autoUdacSeize(final PrivateWireFsmContext context)
    {
        this.delegateNonMockable.autoUdacSeize(context);
    }

    @Override
    public void ctiConference(final ConferenceActionType action)
    {
        this.delegateMockable.ctiConference(action);
    }

    @Override
    public void ctiSeizeResource(final PrivateWireFsmContext context)
    {
        this.delegateMockable.ctiSeizeResource(context);
    }

    @Override
    public void ctiReleaseResource()
    {
        this.delegateMockable.ctiReleaseResource();
    }

    public void setDigits(final String digits)
    {
        this.delegateNonMockable.setDigits(digits);        
    }

    @Override
    public void udacBarge()
    {
        this.delegateMockable.udacBarge();
    }

    @Override
    public void udacBarged(final CallControlConferencedEvent event)
    {
        this.delegateMockable.udacBarged(event);
    }

    @Override
    public void udacCliUpdate(final String name, final String number)
    {
        this.delegateMockable.udacCliUpdate(name,number);
    }

    @Override
    public void ctiCancelTelephonyTones()
    {
        this.delegateMockable.ctiCancelTelephonyTones();
    }

    @Override
    public void udacCpiUpdate(final CallControlInformationEvent event)
    {
        this.delegateMockable.udacCpiUpdate(event);
    }

    @Override
    public void ctiDialDigits(final String digits)
    {
        this.delegateMockable.ctiDialDigits(digits);
    }
}
