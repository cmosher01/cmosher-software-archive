package com.surveysampling.bulkemailer.mta;

import java.util.Properties;

import javax.mail.Session;

/**
 * Adds some functionality regarding JavaMail's <code>Session</code> class.
 * Specifically, provides a static method to get a <code>Session</code>
 * object, properly initialized, based on a given scheme,
 * host, port, and timeout.
 * 
 * @author Chris Mosher
 */
public class MailSession
{
    /**
     * Calls <code>javax.mail.Session.getInstance</code>,
     * with the appropriate properties for the given
     * scheme, host, port, and timeout, and returns the
     * resulting <code>Session</code> object.
     * @param scheme for example, "smtp"
     * @param host for example, "smtp.surveysampling.com"
     * @param port for example, 25
     * @param timeout in milliseconds
     * @return a new, initialized <code>Session</code>
     */
    public static Session getNew(String scheme, String host, int port, int timeout)
    {
        MailSession ms = new MailSession(scheme, host, port, timeout);
        Properties props = new Properties();
        ms.getProperties(props);

        return Session.getInstance(props, null);
    }



    private final String scheme;
    private final String host;
    private final int port;
    private final int timeout;

    /**
     * Initializes a <code>MailSession</code> object.
     * @param scheme for example, "smtp"
     * @param host for example, "smtp.surveysampling.com"
     * @param port for example, 25
     * @param timeout in milliseconds
     */
    protected MailSession(String scheme, String host, int port, int timeout)
    {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    /**
     * Puts the following properties into the
     * given <code>Properties</code> object:
     * <ul>
     * <li><code>mail.transport.protocol</code></li>
     * <li><code>mail.<i>protocol</i>.host</code></li>
     * <li><code>mail.<i>protocol</i>.port</code></li>
     * <li><code>mail.<i>protocol</i>.connectiontimeout</code></li>
     * <li><code>mail.<i>protocol</i>.timeout</code></li>
     * </ul>
     * @param props <code>Properties</code> to add to
     */
    protected void getProperties(Properties props)
    {
        props.put("mail.transport.protocol", scheme);

        props.put(getPropName("host"), host);
        props.put(getPropName("port"), Integer.toString(port));
        props.put(getPropName("connectiontimeout"), Integer.toString(timeout));
        props.put(getPropName("timeout"), Integer.toString(timeout));
    }

    /**
     * Builds a string suitable for use as a <code>MailSession</code> property.
     * The string is of the form:
     * <code>mail.<i>protocol</i>.<i>property</i></code>
     * @param property
     * @return full property key
     */
    protected String getPropName(String property)
    {
        StringBuffer sb = new StringBuffer(32);
        sb.append("mail.");
        sb.append(scheme);
        sb.append(".");
        sb.append(property);
        return sb.toString();
    }
}
