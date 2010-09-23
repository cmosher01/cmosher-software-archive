package com.ipc.uda.service.ejb.mdb;


import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.ipc.uda.event.UdaMessage;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.util.UdaPrincipal;
import com.ipc.uda.types.Event;

/**
 * Message-drive bean for reading from the uda.jms.executableResultQueue JMS queue.
 * Whenever an event is placed onto that queue, this bean consumes it, and places
 * the message on the appropriate user's long-poll response queue.
 * 
 * @author mosherc
 */
@MessageDriven(mappedName = "uda.jms.executableResultQueue")
public class ExecutableResultBean implements MessageListener
{
    @Override
    public void onMessage(final Message message)
    {
        try
        {
            tryMessage(message);
        }
        catch (final Throwable e)
        {
            throw (EJBException)new EJBException().initCause(e);
        }
    }

    private static void tryMessage(final Message message) throws JMSException
    {
        // assume the message we got is our ObjectMessage
        final ObjectMessage objmess = (ObjectMessage)message;

        // assume the ObjectMessage contains our UdaMessage<Event>
        final UdaMessage<Event> udamessage = getUdaMessage(objmess);

        // get the user that the message applies to
        final UdaPrincipal user = udamessage.getUser();
        final String deviceID = udamessage.getDeviceID();

        // look up the user's context (session)
        final UserContext context = UserContextManager.getInstance().getOrCreateContext(new UserID(user, deviceID));

        // put our message onto the user's event queue
        context.getEventQueue().send(udamessage.getMessage());
    }

    @SuppressWarnings("unchecked")
    private static UdaMessage<Event> getUdaMessage(final ObjectMessage objmess) throws JMSException
    {
        return (UdaMessage<Event>)objmess.getObject();
    }
}
