package com.surveysampling.mosher.test;

import java.util.Properties;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

public class OutBox
{
    private static final String DEFAULT_FROM =
        "\"Survey Sampling, Inc.\" <postmaster@surveysampling.com>";

    private final Session mSession;
    private final InternetAddress mReturnAddress;
    private final Transport mTransport;

    public OutBox()
        throws AddressException, NoSuchProviderException, MessagingException
    {
        this("smtp","ntnm.surveysampling.com",26,DEFAULT_FROM);
    }

    public OutBox(String protocol, String host, int port, String from)
        throws AddressException, NoSuchProviderException, MessagingException
    {
        mSession = Session.getInstance(getProperties(protocol,host,port),null);

        mReturnAddress = new InternetAddress(from);

        mTransport = mSession.getTransport();
        mTransport.connect();
    }

    private static Properties getProperties(String protocol, String host, int port)
    {
        Properties props = new Properties();

        props.put("mail.transport.protocol",protocol);
        props.put(getPropName(protocol,"host"),host);
        props.put(getPropName(protocol,"port"),Integer.toString(port));

        return props;
    }

    private static String getPropName(String protocol, String property)
    {
        StringBuffer sb = new StringBuffer(14);
        sb.append("mail.");
        sb.append(protocol);
        sb.append(".");
        sb.append(property);
        return sb.toString();
    }

    public void put(String to, String subject, String message)
        throws MessagingException
    {
        Message msg = new MimeMessage(mSession);

        msg.setFrom(mReturnAddress);
        msg.setRecipient(Message.RecipientType.TO,new InternetAddress(to));
        msg.setSubject(subject);
        msg.setContent(message,"text/plain");
        msg.saveChanges();

        mTransport.sendMessage(msg,msg.getAllRecipients());
    }

    public void close() throws MessagingException
    {
        mTransport.close();
    }

    protected void finalize() throws Throwable
    {
        super.finalize();
        close();
    }
}
