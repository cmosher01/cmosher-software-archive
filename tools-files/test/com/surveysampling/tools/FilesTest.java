package com.surveysampling.tools;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class FilesTest extends TestCase
{
    public FilesTest(String name)
    {
        super(name);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(FilesTest.class);
    }

    public void testRelativePath() throws IOException
    {
        assertRelativePath("x","c:\\temp\\a","c:\\temp\\x");
        assertRelativePath("..\\x","c:\\temp\\test\\a","c:\\temp\\x");
        assertRelativePath("..\\foo\\x","c:\\temp\\test\\a","c:\\temp\\foo\\x");
        assertRelativePath("..\\foo\\bar\\x","c:\\temp\\test\\a","c:\\temp\\foo\\bar\\x");
        assertRelativePath("..\\..\\foo\\bar\\x","c:\\temp\\junk\\test\\a","c:\\temp\\foo\\bar\\x");
        assertRelativePath("x","c:\\temp\\","c:\\temp\\x");
        assertRelativePath("foo\\x","c:\\temp\\","c:\\temp\\foo\\x");
        assertRelativePath("foobar\\x","c:\\temp\\foo\\","c:\\temp\\foobar\\x");
        assertRelativePath("x","\\a","\\x");
        assertRelativePath("x","x","x");
        assertRelativePath("x","a","x");
        assertRelativePath("x","","x");
        assertRelativePath("","\\","\\");
        assertRelativePath("foo","\\foo","\\foo");
    }

    private void assertRelativePath(String expected, String from, String to) throws IOException
    {
        File fFrom = new File(from);
        File fTo = new File(to);

        File fRel = Files.relativePath(fFrom,fTo);
        String sRel = fRel.getPath();

        assertEquals(expected,sRel);
    }
}
