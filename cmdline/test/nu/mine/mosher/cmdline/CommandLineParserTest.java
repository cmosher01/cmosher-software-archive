/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.cmdline;

import java.io.StringReader;
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
        CommandLineParser p = new CommandLineParser(new SyntaxDefinition(new StringReader("")));
        Iterator i = p.parse(new String[] { "-a", "test.dat"});

        assertTrue(i.hasNext());
        Argument a = (Argument)i.next();

        assertTrue(i.hasNext());
        a = (Argument)i.next();

        assertFalse(i.hasNext());
    }
}
