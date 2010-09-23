/**
 * 
 */
package com.ipc.uda.service.ejb.mdb;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;

/**
 * @author mordarsd
 * 
 */
public class CtiEventQueueBeanTestCase
{

    @Test
    public void testProduceMessage() throws NamingException, JMSException
    {
        Hashtable<String, String> props = new Hashtable<String, String>(5);
        InitialContext ctx = null;
        ConnectionFactory fact = null;
        Connection conn = null;
        Session session = null;
        Queue queue = null;
        MessageProducer producer = null;

        props.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        props.put(Context.PROVIDER_URL, "t3://localhost:7001");
        props.put(Context.SECURITY_AUTHENTICATION, "simple");
        props.put(Context.SECURITY_PRINCIPAL, "weblogic");
        props.put(Context.SECURITY_CREDENTIALS, "password");

        try
        {
            ctx = new InitialContext(props);
            fact = (ConnectionFactory) ctx.lookup("uda.jms.queue.CtiEventQueueConnectionFactory");
            if (fact != null)
            {
                conn = fact.createConnection();
                session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                queue = (Queue) ctx.lookup("uda.jms.queue.CtiEventQueue");

                producer = session.createProducer(queue);
                TextMessage msg = session.createTextMessage();
                msg.setText("Test");
                producer.send(msg);

            }
        }
        finally
        {
            if (session != null)
            {
                session.close();
            }
            if (conn != null)
            {
                conn.close();
            }
        }

    }
}
