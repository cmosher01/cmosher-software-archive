package com.surveysampling;

import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@MessageDriven(activationConfig =
{ @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = "/queue/LogMessages") })
public class MDBLog implements MessageListener
{
	public void onMessage(Message msg)
	{
		try
		{
			System.out.println("Received log message: " + ((ObjectMessage) msg).getObject().toString());
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}
}
