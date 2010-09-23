package com.ipc.uda.service.callproc;

import com.ipc.uda.types.LineStatusType;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.callControl.events.CallControlInformationEvent;

/**
 * Defines the interface that contains all mockable actions
 * called by the IcmP2P FSM. This interface is designed to
 * be implemented by a concrete class in production code, or
 * to be mocked (using JMock) in a unit testing environment.
 * 
 * @see IcmP2P
 * @author mosherc
 */
public interface IcmP2PMockableActions
{
    // events to send to UDAC
    void udacLineStatusUpdated(LineStatusType status);
    void udacCpiUpdate(CallControlInformationEvent event);



    // commands to send to CTI
    void ctiAccept();
    void ctiAnswer();
    void ctiClearConnection(EventCause cause);
    void ctiMakeCall();



    // UDAS handling
    void putOntoHandset();
    void removeFromHandset();

    void destroy();
}
