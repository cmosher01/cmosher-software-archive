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
        CommandLineParser p = new CommandLineParser(new String[] { "-a", "test.dat"});
        Iterator i = p.getArguments();
        Argument a = (Argument)i.next();
        Argument a = (Argument)i.next();
        assertFalse(i.hasNext());
    }
}
