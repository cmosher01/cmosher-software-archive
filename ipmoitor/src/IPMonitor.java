/*
 * Created on Aug 26, 2005
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class IPMonitor
{
    private static final String IP_SERVER = "http://checkip.dyndns.org/";
    private static final Pattern PATTERN_WITH_IP_GROUP = Pattern.compile(".*Current IP Address: ([\\.0-9]*).*");

    protected static final Properties props = new Properties();

    private static InetAddress ipLast;



    public static void main(final String[] args) throws IOException
    {
        ipLast = InetAddress.getLocalHost();

        String filenameProps = "ipmonitor.properties";
        if (args.length > 0)
        {
            filenameProps = args[0];
        }
        props.load(new FileInputStream(filenameProps));

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
			public void run()
            {
                try
                {
                    checkIP();
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                    System.err.println("continuing...");
                }
            }
        },0,60L*1000L*Integer.parseInt(props.getProperty("checkMinutes","60")));
    }

    protected static void checkIP() throws MalformedURLException, UnsupportedEncodingException, UnknownHostException, IOException, MessagingException
    {
        final InetAddress ip = getIP();

        if (!ip.equals(ipLast))
        {
            System.err.println("IP has changed from "+ipLast.getHostAddress()+" to "+ip.getHostAddress());
            ipLast = ip;
            notifyChangedIP();
        }
    }

    private static InetAddress getIP() throws MalformedURLException, UnsupportedEncodingException, IOException, UnknownHostException
    {
        final URL url = new URL(IP_SERVER);
        final BufferedReader ipPage = new BufferedReader(new InputStreamReader(url.openStream(),"US-ASCII"));

        final String sLine = ipPage.readLine();
        InetAddress ip = getIPFromLine(sLine);
        ipPage.close();
        return ip;
    }

    private static InetAddress getIPFromLine(final String line) throws UnknownHostException
    {
        final Matcher matcher = PATTERN_WITH_IP_GROUP.matcher(line);
        if (!matcher.matches())
        {
            throw new IllegalStateException("format of web page has changed");
        }

        final String sIP = matcher.group(1);

        return InetAddress.getByName(sIP);
    }

    private static void notifyChangedIP() throws MessagingException
    {
        final Properties propsSession = new Properties();
        propsSession.put("mail.transport.protocol","smtp");
        propsSession.put("mail.smtp.host",props.getProperty("smtpHost"));
        propsSession.put("mail.smtp.auth","true");
        propsSession.put("mail.smtp.port","465");
        propsSession.put("mail.smtp.socketFactory.port","465");
        propsSession.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        propsSession.put("mail.smtp.socketFactory.fallback","false");
        propsSession.put("mail.smtp.starttls.enable","true");
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final Session session = Session.getDefaultInstance(propsSession,new Authenticator()
            {
                @Override
				protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(props.getProperty("username"),props.getProperty("password"));
                }
            });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(props.getProperty("emailFrom")));
        msg.setSubject(ipLast.getHostAddress());
        msg.setContent(ipLast.getHostAddress(),"text/plain");
        msg.addRecipients(Message.RecipientType.TO,InternetAddress.parse(props.getProperty("emailFrom")));
        Transport.send(msg);
    }
}
