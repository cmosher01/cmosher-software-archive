package com.ipc.uda.service.ejb.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Message-Driven Bean implementation class for: CtiEventQueueBean
 *
 */
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"
		) }, 
		mappedName = "uda.jms.ctiEventQueue")
public class CtiEventQueueBean implements MessageListener {

    /**
     * Default constructor. 
     */
    public CtiEventQueueBean() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage( Message message ) {
        System.out.println( "CtiEventQueueBean::onMessage() - " + message );
	
    }

}
