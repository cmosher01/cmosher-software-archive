package nu.mine.mosher.util.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import nu.mine.mosher.util.text.exception.IllegalQuoteException;
import nu.mine.mosher.util.text.exception.UnmatchedQuoteException;

import junit.framework.TestCase;

public class ExcelCSVFieldizerTest extends TestCase
{
    public void testParse1() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("a");
        r.add("b");
        r.add("c");
        testOneRow("a,b,c",r);
    }

    public void testParse2() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("a");
        r.add("");
        r.add("c");
        testOneRow("a,,c",r);
    }

    public void testParse3() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("a");
        r.add("abc");
        r.add("c");
        testOneRow("a,abc,c",r);
    }

    public void testParse4() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("abc");
        r.add("b");
        r.add("c");
        testOneRow("abc,b,c",r);
    }

    public void testParse5() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("abc");
        r.add("b");
        r.add("cde");
        testOneRow("abc,b,cde",r);
    }

    public void testParse6() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("");
        r.add("b");
        r.add("cde");
        testOneRow(",b,cde",r);
    }

    public void testParse7() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("abc");
        r.add("b");
        r.add("");
        testOneRow("abc,b,",r);
    }

    public void testParse8() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("abc");
        r.add("");
        r.add("");
        testOneRow("abc,,",r);
    }

    public void testParse9() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("");
        testOneRow("",r);
    }

    public void testParse10() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("");
        testOneRow("\"\"",r);
    }

    public void testParse11() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("");
        r.add("");
        testOneRow(",",r);
    }

    public void testParse12() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("abc");
        r.add("def");
        testOneRow("\"abc\",\"def\"",r);
    }

    public void testParse13() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("ab\"c");
        r.add("def");
        testOneRow("\"ab\"\"c\",\"def\"",r);
    }

    public void testParse14() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("ab,c");
        r.add("def");
        testOneRow("\"ab,c\",\"def\"",r);
    }

    public void testParse15() throws Throwable
    {
        List<String> r = new ArrayList<String>();
        r.add("ab,c");
        r.add("def");
        r.add("ghi");
        r.add("x");
        testOneRow("\"ab,c\",\"def\",ghi,x",r);
    }



    public void testOneRow(String row, List<String> fieldsExpected) throws IllegalQuoteException, UnmatchedQuoteException
    {
        String[] r = fieldsExpected.toArray(new String[fieldsExpected.size()]);
        final ExcelCSVFieldizer fieldizer = new ExcelCSVFieldizer(row);
        final Collection<String> rField = new ArrayList<String>();
        fieldizer.getFields(rField);
        Iterator i = rField.iterator();
        for (int j = 0; j < r.length; ++j)
        {
            assertTrue(i.hasNext());
            assertEquals(r[j],i.next());
        }
        assertFalse(i.hasNext());
        try
        {
            i.next();
            fail("should throw NoSuchElementException");
        }
        catch (NoSuchElementException shouldBeThrown)
        {
        	// OK
        }
    }



    public void testParse17() throws Throwable
    {
        final ExcelCSVFieldizer fieldizer = new ExcelCSVFieldizer("\"test\"bad\"");
        final Collection<String> rField = new ArrayList<String>();
        fieldizer.getFields(rField);
        Iterator i = rField.iterator();

        try
        {
            i.next();
            fail("should throw IllegalQuoteException");
        }
        catch (NoSuchElementException e)
        {
            if (!(e.getCause() instanceof IllegalQuoteException))
            {
                fail("should throw IllegalQuoteException");
            }
        }
    }

    public void testParse18() throws Throwable
    {
        final ExcelCSVFieldizer fieldizer = new ExcelCSVFieldizer("\"test,unma,tchedquote");
        final Collection<String> rField = new ArrayList<String>();
        fieldizer.getFields(rField);
        Iterator i = rField.iterator();
        try
        {
            i.next();
            fail("should throw UnmatchedQuoteException");
        }
        catch (NoSuchElementException e)
        {
            if (!(e.getCause() instanceof UnmatchedQuoteException))
            {
                fail("should throw UnmatchedQuoteException");
            }
        }
    }

    public void testParse19() throws Throwable
    {
        final ExcelCSVFieldizer fieldizer = new ExcelCSVFieldizer("test\"bad");
        final Collection<String> rField = new ArrayList<String>();
        fieldizer.getFields(rField);
        Iterator i = rField.iterator();

        try
        {
            i.next();
            fail("should throw IllegalQuoteException");
        }
        catch (NoSuchElementException e)
        {
            if (!(e.getCause() instanceof IllegalQuoteException))
            {
                fail("should throw IllegalQuoteException");
            }
        }
    }
}
