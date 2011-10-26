package nu.mine.mosher.util.text;

import nu.mine.mosher.util.text.exception.IllegalQuoteException;
import nu.mine.mosher.util.text.exception.UnmatchedQuoteException;
import junit.framework.TestCase;

public class ExcelCSVParserTest extends TestCase
{
    public ExcelCSVParserTest(String name)
    {
        super(name);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ExcelCSVParserTest.class);
    }

    public void testParse1() throws Throwable
    {
        StringBuilder s = new StringBuilder("a,b,c");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("a",parser.getOneValue());
        assertEquals(2,parser.getNextPos());

        assertEquals("b",parser.getOneValue());
        assertEquals(4,parser.getNextPos());

        assertEquals("c",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse2() throws Throwable
    {
        StringBuilder s = new StringBuilder("a,,c");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("a",parser.getOneValue());
        assertEquals(2,parser.getNextPos());

        assertEquals("",parser.getOneValue());
        assertEquals(3,parser.getNextPos());

        assertEquals("c",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse3() throws Throwable
    {
        StringBuilder s = new StringBuilder("a,abc,c");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("a",parser.getOneValue());
        assertEquals(2,parser.getNextPos());

        assertEquals("abc",parser.getOneValue());
        assertEquals(6,parser.getNextPos());

        assertEquals("c",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse4() throws Throwable
    {
        StringBuilder s = new StringBuilder("abc,a,c");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("abc",parser.getOneValue());
        assertEquals(4,parser.getNextPos());

        assertEquals("a",parser.getOneValue());
        assertEquals(6,parser.getNextPos());

        assertEquals("c",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse5() throws Throwable
    {
        StringBuilder s = new StringBuilder("abc,a,cdef");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("abc",parser.getOneValue());
        assertEquals(4,parser.getNextPos());

        assertEquals("a",parser.getOneValue());
        assertEquals(6,parser.getNextPos());

        assertEquals("cdef",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse6() throws Throwable
    {
        StringBuilder s = new StringBuilder(",a,cdef");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("",parser.getOneValue());
        assertEquals(1,parser.getNextPos());

        assertEquals("a",parser.getOneValue());
        assertEquals(3,parser.getNextPos());

        assertEquals("cdef",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse7() throws Throwable
    {
        StringBuilder s = new StringBuilder("abc,a,");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("abc",parser.getOneValue());
        assertEquals(4,parser.getNextPos());

        assertEquals("a",parser.getOneValue());
        assertEquals(6,parser.getNextPos());

        assertEquals("",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse8() throws Throwable
    {
        StringBuilder s = new StringBuilder("abc,,");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("abc",parser.getOneValue());
        assertEquals(4,parser.getNextPos());

        assertEquals("",parser.getOneValue());
        assertEquals(5,parser.getNextPos());

        assertEquals("",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse9() throws Throwable
    {
        StringBuilder s = new StringBuilder("x");
        try
        {
            new ExcelCSVParser(s,-1);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalStateException e)
        {
        }
        new ExcelCSVParser(s,0);
        new ExcelCSVParser(s,1);
        try
        {
            new ExcelCSVParser(s,2);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalStateException e)
        {
        }
    }

    public void testParse10() throws Throwable
    {
        StringBuilder s = new StringBuilder(256);
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse11() throws Throwable
    {
        StringBuilder s = new StringBuilder(",");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("",parser.getOneValue());
        assertEquals(1,parser.getNextPos());

        assertEquals("",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse12() throws Throwable
    {
        StringBuilder s = new StringBuilder("\"abc\",\"def\"");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("abc",parser.getOneValue());
        assertEquals(6,parser.getNextPos());

        assertEquals("def",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse13() throws Throwable
    {
        StringBuilder s = new StringBuilder("\"ab\"\"c\",\"def\"");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("ab\"c",parser.getOneValue());
        assertEquals(8,parser.getNextPos());

        assertEquals("def",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse14() throws Throwable
    {
        StringBuilder s = new StringBuilder("\"ab,c\",\"def\"");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("ab,c",parser.getOneValue());
        assertEquals(7,parser.getNextPos());

        assertEquals("def",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse15() throws Throwable
    {
        StringBuilder s = new StringBuilder("\"ab,c\",\"def\",ghi,x");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("ab,c",parser.getOneValue());
        assertEquals(7,parser.getNextPos());

        assertEquals("def",parser.getOneValue());
        assertEquals(13,parser.getNextPos());

        assertEquals("ghi",parser.getOneValue());
        assertEquals(17,parser.getNextPos());

        assertEquals("x",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse16() throws Throwable
    {
        StringBuilder s = new StringBuilder("\"\"");
        ExcelCSVParser parser = new ExcelCSVParser(s);

        assertEquals("",parser.getOneValue());
        assertEquals(-1,parser.getNextPos());
    }

    public void testParse17() throws Throwable
    {
        StringBuilder s = new StringBuilder("\"test\"bad\"");
        ExcelCSVParser parser = new ExcelCSVParser(s);
        try
        {
            parser.getOneValue();
            fail("should throw IllegalQuoteException");
        }
        catch (IllegalQuoteException shouldBeThrown)
        {
        }
    }

    public void testParse18() throws Throwable
    {
        StringBuilder s = new StringBuilder("\"test,unma,tchedquote");
        ExcelCSVParser parser = new ExcelCSVParser(s);
        try
        {
            parser.getOneValue();
            fail("should throw UnmatchedQuoteException");
        }
        catch (UnmatchedQuoteException shouldBeThrown)
        {
        }
    }

    public void testParse19() throws Throwable
    {
        StringBuilder s = new StringBuilder("test\"abc");
        ExcelCSVParser parser = new ExcelCSVParser(s);
        try
        {
            parser.getOneValue();
            fail("should throw IllegalQuoteException");
        }
        catch (IllegalQuoteException shouldBeThrown)
        {
        }
    }

    public void testParse20() throws Throwable
    {
        StringBuilder s = new StringBuilder(256);
        try
        {
            new ExcelCSVParser(s,-1);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalStateException e)
        {
        }
        new ExcelCSVParser(s,0);
        try
        {
            new ExcelCSVParser(s,1);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalStateException e)
        {
        }
    }
}
