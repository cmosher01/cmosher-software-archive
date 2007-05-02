package com.surveysampling.bulkemailer.mta;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.ParseException;

import com.surveysampling.email.Email;
import com.surveysampling.email.SendMail;
import com.surveysampling.util.Flag;

/**
 * Handles a connection to an MTA. Contains a <code>Session</code> and
 * <code>Transport</code> object (from the JavaMail API). Call the
 * <code>connect</code> method to connect to the MTA. Call the <code>send</code>
 * method to send an Email. Call the <code>close</code> method
 * after all messages have been sent. This class writes all exceptions
 * to System.err (in addition to handling them or re-throwing them as
 * appropriate).
 * 
 * @author Chris Mosher
 */
public class MtaConnection
{
    private final String scheme;
    private final String host;
    private final int port;
    private final int timeout;
    private final Flag mFlagShutdown;



    private Session mSession;
    private Transport mTransport;



    /**
     * Initializes the MTA connection.
     * @param scheme protocol, for example, smtp
     * @param host domain name or ip address of host of MTA
     * @param port port number on host
     * @param timeout the TCP timeout (in milliseconds)
     * @param flagShutdown a reference to a Flag that the caller can set
     * to true to indicate that the current call to <code>send</code> should
     * throw an <code>InterruptedException</code>
     */
    public MtaConnection(String scheme, String host, int port, int timeout, Flag flagShutdown)
    {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.mFlagShutdown = flagShutdown;
    }

    /**
     * Connects to the MTA. If the connection has already
     * been established before, then this method closes
     * that connection first.
     * @throws MessagingException thrown by the JavaMail API
     */
    public void connect() throws MessagingException
    {
        try
        {
            // close any existing connection
            close();
            // create our Session and Transport
            mSession = MailSession.getNew(scheme,host,port,timeout);
            mTransport = mSession.getTransport();
            // connect
            mTransport.connect();
        }
        catch (MessagingException e)
        {
            logError(e, "Exception while connecting to " + host);
            throw e;
        }
    }

    /**
     * Sends an email message to the MTA.
     * @param email
     * @throws MessagingException thrown by the JavaMail API
     * @throws InterruptedException if the current thread is interrupted, or
     * if the caller sets the flagShutdown Flag (as passed to the constructor) to true
     * @throws InvalidEmailException thrown when the JavaMail API indicates
     * a bad message (as opposed to some other kind of MessagingException).
     * Specifically, this will wrap a SendFailedException or a ParseException.
     */
    public void send(Email email) throws MessagingException, InterruptedException, InvalidEmailException
    {
        boolean sent = false;
        while (!sent)
        {
            try
            {
                sendDetectShutdown(email);
                sent = true;
            }
            catch (IllegalStateException e)
            {
                /*
                 * This can happen if another thread calls our close
                 * method before we actually send the message. Treat
                 * this as an interrupt request.
                 */
                logError(e,"Interrupted while sending mail.");
                throw wrapInInterruptedException(e);
            }
            catch (SendFailedException e)
            {
                /*
                 * SendFailedException gets thrown only when we try to
                 * send to an invalid address. In this case, we just
                 * want to indicate the message as "failed send." We
                 * don't want to try to send it again, and we don't
                 * want to shut down any MTAs.
                 */
                handleInvalidEmail(email,e);
            }
            catch (ParseException e)
            {
                // handle the same as SendFailedException
                handleInvalidEmail(email,e);
            }
            catch (MessagingException e)
            {
                logError(e, "Exception while sending mail to " + host);
                throw e;
            }
        }
    }

    private void handleInvalidEmail(Email email, Throwable e) throws InvalidEmailException
    {
        logError(e);

        Address[] raddr = email.getRecipients();
        String sTo = "";
        if (raddr.length > 0)
        {
            sTo = raddr[0].toString();
        }
        throw new InvalidEmailException(sTo,e);
    }

    /**
     * Sends an email message to the MTA.
     * @param email the Email to send
     * @throws MessagingException thrown by the JavaMail API
     * @throws InterruptedException if the flagShutdown Flag
     * (as passed to the constructor) gets set to true
     */
    protected void sendDetectShutdown(Email email) throws MessagingException, InterruptedException
    {
        try
        {
            Message msg = SendMail.buildMimeMsg(mSession, email);
            mTransport.sendMessage(msg, msg.getAllRecipients());
        }
        finally
        {
            if (mFlagShutdown.isTrue())
            {
                throw new InterruptedException("Job abort request detected while sending mail.");
            }
        }
    }

    /**
     * Closes the connection to the MTA. If the connection
     * has already been closed, this method does nothing.
     */
    public void close()
    {
        if (mTransport == null)
        {
            return;
        }

        try
        {
            mTransport.close();
        }
        catch (Throwable ignore)
        {
            ignore.printStackTrace();
        }
        mTransport = null;
    }



    /**
     * Wraps the given exception in a new InterruptedException.
     * @param cause an exception to be wrapped
     * @return the new InterruptedException
     */
    private static InterruptedException wrapInInterruptedException(Throwable cause)
    {
        InterruptedException ex = new InterruptedException();
        ex.initCause(cause);
        return ex;
    }

    /**
     * Same as <code>logError(e,null);</code>
     * @param e
     */
    private static void logError(Throwable e)
    {
        logError(e,null);
    }

    /**
     * Writes a stack trace to System.err, with an
     * optional message. Also prints the name of
     * the current thread, and a line of hyphens.
     * @param e the exception to be printed
     * @param s a message to be printed, or null
     */
    private static void logError(Throwable e, String s)
    {
        synchronized (System.err)
        {
            System.err.print("(thread ");
            System.err.print(Thread.currentThread().getName());
            System.err.println(")");
            if (s != null)
            {
                System.err.println(s);
            }
            e.printStackTrace();
            System.err.println("---------------------------------------------------------------------");
        }
    }
}
