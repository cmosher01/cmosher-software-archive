/*
 * Created on Jun 1, 2005
 */
package com.surveysampling.emailpanel.counts.api.internal.sql;

import junit.framework.TestCase;

/**
 * Runs a few test of <code>SQLConditionBuilder</code>.
 * 
 * @author Chris Mosher
 */
public class SQLConditionBuilderTest extends TestCase
{
    /**
     * 
     */
    public void testNone()
    {
        final StringBuffer sb = new StringBuffer();
        new SQLConditionBuilder(sb,"AND");
        assertTrue(sb.length() == 0);
    }

    /**
     * 
     */
    public void testEmpty()
    {
        final StringBuffer sb = new StringBuffer();
        final SQLConditionBuilder bld = new SQLConditionBuilder(sb,"AND");
        bld.appendTerm(new StringBuffer());
        assertTrue(sb.length() == 0);
    }

    /**
     * 
     */
    public void testNull()
    {
        final StringBuffer sb = new StringBuffer();
        final SQLConditionBuilder bld = new SQLConditionBuilder(sb,"AND");
        bld.appendTerm(null);
        assertTrue(sb.length() == 0);
    }

    /**
     * 
     */
    public void testOneTerm()
    {
        final StringBuffer sb = new StringBuffer();
        final SQLConditionBuilder bld = new SQLConditionBuilder(sb,"AND");
        bld.appendTerm(new StringBuffer("TERM"));
        assertEquals("(TERM)",sb.toString());
    }

    /**
     * 
     */
    public void testTwoTerms()
    {
        final StringBuffer sb = new StringBuffer();
        final SQLConditionBuilder bld = new SQLConditionBuilder(sb,"AND");
        bld.appendTerm(new StringBuffer("TERM1"));
        bld.appendTerm(new StringBuffer("TERM2"));
        assertEquals("(TERM1) AND \n(TERM2)",sb.toString());
    }

    /**
     * 
     */
    public void testThreeTerms()
    {
        final StringBuffer sb = new StringBuffer();
        final SQLConditionBuilder bld = new SQLConditionBuilder(sb,"AND");
        bld.appendTerm(new StringBuffer("TERM1"));
        bld.appendTerm(new StringBuffer("TERM2"));
        bld.appendTerm(new StringBuffer("TERM3"));
        assertEquals("(TERM1) AND \n(TERM2) AND \n(TERM3)",sb.toString());
    }
}
