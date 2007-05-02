/*
 * Created on Jun 4, 2004
 */
package com.surveysampling.bulkemailer.mta;

/**
 * Indicates an invalid email (address or header).
 * This is used to wrap javax.mail.SendFailedException
 * and javax.mail.internet.ParseException, to indicate
 * known (and possibly acceptable) types of exceptions
 * that can occur while sending email, to distinguish
 * them from other (possibly fatal) exceptions (such
 * as network failure).
 * 
 * @author Chris Mosher
 */
public class InvalidEmailException extends Exception
{
    private final String emailAddress;

    /**
     * Constructs a new exception.
     * @param emailAddress the email address that is invalid
     */
    public InvalidEmailException(String emailAddress)
    {
        super("Invalid email address \""+emailAddress+"\"");
        this.emailAddress = emailAddress;
    }

    /**
     * Constructs a new exception.
     * @param emailAddress the email address that is invalid
     * @param cause underlying exception
     */
    public InvalidEmailException(String emailAddress, Throwable cause)
    {
        super("Invalid email address \""+emailAddress+"\"",cause);
        this.emailAddress = emailAddress;
    }

    /**
     * Returns the email address.
     * @return email address
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }
}
