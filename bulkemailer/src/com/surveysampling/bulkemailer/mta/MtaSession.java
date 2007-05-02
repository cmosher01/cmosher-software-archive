package com.surveysampling.bulkemailer.mta;

import javax.mail.MessagingException;

import com.surveysampling.email.Email;
import com.surveysampling.util.Flag;

/**
 * Represents a single-threaded session for
 * sending emails to an MTA. It uses its manager
 * (the <code>MtaManager</code> reference passed into the
 * constructor) to get an <code>Mta</code>,
 * connects to it when necessary, and uses it
 * to send the email. It also will mark the
 * <code>Mta</code> as bad if a communication
 * error is detected (while connecting or sending).
 * 
 * @author Chris Mosher
 */
public class MtaSession
{
    private final MtaManager mMgr;
    private Flag mFlagShutdown;

    private MtaConnection mMtaConnection;
    private Mta mMta;



    /**
     * Initializes an <code>MtaSession</code>, so it has
     * the given manager.
     * @param mgr the <code>MtaManager</code> for this session
     */
    public MtaSession(MtaManager mgr)
    {
        mMgr = mgr;
    }



    /**
     * Sets the shutdown flag. The flag is passed
     * internally to the <code>MtaConnection</code> object (when
     * this session creates one).
     * @param flagShutdown
     */
    public void setFlagShutdown(Flag flagShutdown)
    {
        mFlagShutdown = flagShutdown;
    }

    /**
     * Sends an email.
     * @param msg the email to send
     * @param minTier the minimum tier of MTA to use
     * @throws InvalidEmailException if an email is invalid (such
     * as a bad email address).
     * @throws InterruptedException if this job is terminating
     */
    public void send(Email msg, int minTier) throws InvalidEmailException, InterruptedException
    {
        do
        {
            findMta(minTier);
            sendOrMarkBad(msg);
        }
        while (mMta == null);
    }

    /**
     * Closes this <code>MtaSession</code>, and frees
     * its allocated resources. If a connection to an
     * MTA is open, the connection is closed.
     */
    public void close()
    {
        if (mMtaConnection != null)
        {
            mMtaConnection.close();
            mMtaConnection = null;
            mMta = null;
        }
    }



    /**
     * Finds the MTA to use for sending emails.
     * @param minTier the minimum tier to use
     * @throws InterruptedException if this job is terminating
     */
    protected void findMta(int minTier) throws InterruptedException
    {
        do
        {
            /*
             * See which MTA we are supposed to use to
             * send the current message (ask our manager).
             * In the (hopefully rare) case where there is
             * no sendable MTA available, we close our (old)
             * connection, and block here until
             * one becomes available.
             */
            Mta mta = mMgr.getSendableMta(minTier);
            if (mta == null)
            {
                close();
                mta = mMgr.waitForSendableMta(minTier);
            }

            /*
             * If we didn't have an MTA from last time, or
             * if the one we had last time is different than
             * the one we are supposed to use this time,
             * then close the last connection, and make a new
             * connection to the current MTA.
             */
            if (mMta == null || !mMta.equals(mta))
            {
                close();
                mMta = mta;
                connectOrMarkBad();
            }
        }
        while (mMta == null);
    }

    /**
     * Try to connect to the MTA. If we cannot connect
     * to it, then mark the MTA as bad.
     */
    protected void connectOrMarkBad()
    {
        try
        {
            MtaState mt = mMta.getState();
            mMtaConnection = new MtaConnection(mt.mScheme, mt.mHost, mt.mPort, mt.mTimeout, mFlagShutdown);
            mMtaConnection.connect();
        }
        catch (MessagingException e)
        {
            mMta.setBadIO(true);
            close();
        }
    }

    /**
     * Try to send the message to the MTA.
     * There are three types of exceptions that could happen.
     * An <code>InterruptedException</code> indicates the job is
     * aborting, so we just let that propagate.
     * An <code>InvalidEmailException</code> indicates
     * a bad message (either address or headers), which
     * we let propagate. Any <code>MessagingException</code>,
     * we assume, indicates that we cannot communicate
     * with our MTA, so we mark that MTA as bad (and eat
     * the exception).
     * @param msg the email to send
     * @throws InvalidEmailException invalid message
     * @throws InterruptedException job is aborting
     */
    protected void sendOrMarkBad(Email msg) throws InvalidEmailException, InterruptedException
    {
        try
        {
            mMtaConnection.send(msg);
            mMta.increment();
        }
        catch (MessagingException e)
        {
            mMta.setBadIO(true);
            close();
        }
    }
}
