/*
 * Created on April 19, 2005
 */
package nu.mine.mosher.gedcom.date.parser;

import java.io.StringReader;
import junit.framework.TestCase;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class GedcomDateValueParserTest extends TestCase
{
    public GedcomDateValueParserTest(final String name)
    {
        super(name);
    }

    public static void main(final String[] args)
    {
        junit.textui.TestRunner.run(GedcomDateValueParserTest.class);
    }

	public void testDateValue() throws ParseException
	{
		String s = "1 JAN 2001";
		GedcomDateValueParser parser = new GedcomDateValueParser(new StringReader(s));
		parser.DateValue();
	}
}
