/*
 * Created on May 20, 2004
 */
package com.surveysampling.bulkemailer.mta;

import java.util.Properties;

import junit.framework.TestCase;

public class MailSessionTest extends TestCase
{
    public MailSessionTest(String name)
    {
        super(name);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(MailSessionTest.class);
    }

    public void testGetProperties()
    {
        MailSession ms = new MailSession("smtp","mail.example.com",99,1000);
        Properties props = new Properties();
        ms.getProperties(props);
        assertEquals("smtp",props.get("mail.transport.protocol"));
        assertEquals("mail.example.com",props.get("mail.smtp.host"));
        assertEquals("99",props.get("mail.smtp.port"));
        assertEquals("1000",props.get("mail.smtp.connectiontimeout"));
        assertEquals("1000",props.get("mail.smtp.timeout"));
        assertEquals(5,props.size());
    }

    public void testGetPropName()
    {
        MailSession ms = new MailSession("smtp","mail.example.com",25,1000);
        assertEquals("mail.smtp.test",ms.getPropName("test"));
        assertEquals("mail.smtp.foo",ms.getPropName("foo"));
    }
}
