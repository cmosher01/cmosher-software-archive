/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.cmdline;

import java.util.Iterator;

import junit.framework.TestCase;

/**
 * @author Chris Mosher
 */
public class CommandLineParserTest extends TestCase
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CommandLineParserTest.class);
    }

    public void testCommandLineParser()
    {
        CommandLineParser p = new CommandLineParser();
        p.parse(new String[] { "-a", "test.dat"});
        Iterator i = p.getArguments();

        assertTrue(i.hasNext());
        Argument a = (Argument)i.next();
        assertEquals("a",a.getName());
        assertTrue(a.isOption());
        assertTrue(a.isSpecified());

        assertTrue(i.hasNext());
        a = (Argument)i.next();

        assertFalse(i.hasNext());
    }
}
