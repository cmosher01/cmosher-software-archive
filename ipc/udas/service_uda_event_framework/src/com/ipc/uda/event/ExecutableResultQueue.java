package com.ipc.uda.event;


import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.ipc.uda.service.util.NamingUtil;
import com.ipc.uda.service.util.UdaEnvironmentException;
import com.ipc.uda.service.util.UdaPrincipal;
import com.ipc.uda.service.util.logging.Log;


/**
 * This class has a static method that allows a consumer to place
 * a {@link Serializable} object, for a given user, onto the uda.jms.executableResultQueue
 * JMS queue.
 * 
 * @author mosherc
 * @param <T> the type of {@link Serializable} object to send
 */
public class ExecutableResultQueue<T extends Serializable>
{
    private static final Destination queue = getMessageQueue();
    private static Destination getMessageQueue()
    {
        Context ctx = null;
        try
        {
            ctx = new InitialContext();
            return (Destination)ctx.lookup("uda.jms.executableResultQueue");
        }
        catch (final Throwable e)
        {
            throw new UdaEnvironmentException(
                "Cannot find JMS queue: uda.jms.executableResultQueue; be sure it is configured in the application server", e);
        }
        finally
        {
            NamingUtil.closeContext(ctx);
        }
    }



    private static final ConnectionFactory jmsConnectionFactory = getConnectionFactory();
    private static ConnectionFactory getConnectionFactory()
    {
        Context ctx = null;
        try
        {
            ctx = new InitialContext();
            return (ConnectionFactory)ctx.lookup("uda.jms.connectionFactory");
        }
        catch (final Throwable e)
        {
            throw new UdaEnvironmentException(
                "Cannot find JMS connection factory: uda.jms.connectionFactory; be sure it is configured in the application server", e);
        }
        finally
        {
            NamingUtil.closeContext(ctx);
        }
    }


    /**
     * Places the given {@link Serializable} object onto the uda.jms.executableResultQueue
     * JMS queue, and associates it with the given user.
     * @param <T> the type of {@link Serializable} object to send
     * @param result the Serializable object to send
     * @param user the UdaPrinicipal that the message is for
     */
    public static<T extends Serializable> void send(final T result, final UdaPrincipal user, final String deviceID)
    {
        Connection connection = null;
        try
        {
            connection = jmsConnectionFactory.createConnection();
            final Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

            final MessageProducer producer = session.createProducer(queue);
            final UdaMessage<T> udamessage = new UdaMessage<T>(user, deviceID, result);
            final Message message = session.createObjectMessage(udamessage);

            
            
            producer.send(message);
        }
        catch (final JMSException e)
        {
            Log.logger().logEvent("UDAS","ExecutableResultQueueSendError", new Object[] {result}, e);
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (final Throwable e)
                {
                    Log.logger().debug("Exception occurred while closing JMS connection; ignoring it", e);
                }
            }
        }
    }
}
