/*
 * Created on Jun 8, 2005
 */
package com.surveysampling.emailpanel.counts.api.criteria;

import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

import com.surveysampling.emailpanel.counts.api.internal.DisjointRange;

/**
 * Runs test of <code>EpanCountReportBuilder</code>.
 * Currently, it only tests the income range method.
 * 
 * @author Chris Mosher
 */
public class EpanCountReportBuilderTest extends TestCase
{

    /**
     * Test income string.
     */
    public void testIncomeRanges()
    {
        testOneIncomeString("",false,false,false,false,false,false,false,false,false);
        testOneIncomeString("$150K+",false,false,false,false,false,false,false,false,true);
        testOneIncomeString("$100K+",false,false,false,false,false,false,false,true,true);
        testOneIncomeString("$75K+",false,false,false,false,false,false,true,true,true);
        testOneIncomeString("$60K+",false,false,false,false,false,true,true,true,true);
        testOneIncomeString("$50K+",false,false,false,false,true,true,true,true,true);
        testOneIncomeString("$40K+",false,false,false,true,true,true,true,true,true);
        testOneIncomeString("$30K+",false,false,true,true,true,true,true,true,true);
        testOneIncomeString("$20K+",false,true,true,true,true,true,true,true,true);
        testOneIncomeString("$50K-60K",false,false,false,false,true,false,false,false,false);
        testOneIncomeString("$100K-150K",false,false,false,false,false,false,false,true,false);
        testOneIncomeString("under $150K",true,true,true,true,true,true,true,true,false);
        testOneIncomeString("$20K-150K",false,true,true,true,true,true,true,true,false);
        testOneIncomeString("$75K-150K",false,false,false,false,false,false,true,true,false);
        testOneIncomeString("$60K-150K",false,false,false,false,false,true,true,true,false);
        testOneIncomeString("$50K+",false,false,false,false,true,true,true,true,true);
        testOneIncomeString("under $20K",true,false,false,false,false,false,false,false,false);
        testOneIncomeString("under $30K",true,true,false,false,false,false,false,false,false);
        testOneIncomeString("$20K-40K, or $60K-150K",false,true,true,false,false,true,true,true,false);
        testOneIncomeString("under $20K, or $100K+",true,false,false,false,false,false,false,true,true);
        testOneIncomeString("$20K-30K",false,true,false,false,false,false,false,false,false);
        testOneIncomeString("under $50K, or $60K+",true,true,true,true,false,true,true,true,true);
        testOneIncomeString("any",true,true,true,true,true,true,true,true,true);
    }

    private void testOneIncomeString(
        final String sExpected,
        final boolean b0,
        final boolean b20,
        final boolean b30,
        final boolean b40,
        final boolean b50,
        final boolean b60,
        final boolean b75,
        final boolean b100,
        final boolean b150)
    {
        final StringBuffer buf = new StringBuffer();

        final SortedSet setRangeIncome = new TreeSet();
        if (b0) setRangeIncome.add(new DisjointRange(0,20));
        if (b20) setRangeIncome.add(new DisjointRange(20,30));
        if (b30) setRangeIncome.add(new DisjointRange(30,40));
        if (b40) setRangeIncome.add(new DisjointRange(40,50));
        if (b50) setRangeIncome.add(new DisjointRange(50,60));
        if (b60) setRangeIncome.add(new DisjointRange(60,75));
        if (b75) setRangeIncome.add(new DisjointRange(75,100));
        if (b100) setRangeIncome.add(new DisjointRange(100,150));
        if (b150) setRangeIncome.add(new DisjointRange(150,Integer.MAX_VALUE));

        final SortedSet setJoined = DisjointRange.conjoinSet(setRangeIncome);

        EpanCountReportBuilder.appendIncomeRanges(setJoined,buf);

        assertEquals(sExpected,buf.toString());
    }
}
