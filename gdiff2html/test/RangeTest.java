/*
 * Created on Aug 25, 2004
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class RangeTest extends TestCase
{
    /**
     * 
     */
    public void testDisjoint1()
    {
        Range a = new Range(2,3);
        Range b = new Range(0,1);
        shouldBe(a,b,new Range[]{new Range(2,3)},new Range[]{new Range(0,1)});
    }

    /**
     * 
     */
    public void testDisjoint2()
    {
        Range a = new Range(2,3);
        Range b = new Range(0,1);
        shouldBe(b,a,new Range[]{new Range(0,1)},new Range[]{new Range(2,3)});
    }

    /**
     * 
     */
    public void testEqual()
    {
        Range a = new Range(0,3);
        Range b = new Range(0,3);
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        shouldBe(a,b,new Range[]{new Range(0,3)},new Range[]{new Range(0,3)});
    }

    /**
     * 
     */
    public void testOverlap1()
    {
        Range a = new Range(1,3);
        Range b = new Range(0,2);
        shouldBe(a,b,new Range[]{new Range(1,2),new Range(3,3)},new Range[]{new Range(0,0),new Range(1,2)});
    }

    /**
     * 
     */
    public void testOverlap2()
    {
        Range a = new Range(1,3);
        Range b = new Range(0,2);
        shouldBe(b,a,new Range[]{new Range(0,0),new Range(1,2)},new Range[]{new Range(1,2),new Range(3,3)});
    }

    /**
     * 
     */
    public void testProperSubset1()
    {
        Range a = new Range(1,2);
        Range b = new Range(0,3);
        shouldBe(a,b,new Range[]{new Range(1,2)},new Range[]{new Range(0,0),new Range(1,2),new Range(3,3)});
    }

    /**
     * 
     */
    public void testProperSubset2()
    {
        Range a = new Range(1,2);
        Range b = new Range(0,3);
        shouldBe(b,a,new Range[]{new Range(0,0),new Range(1,2),new Range(3,3)},new Range[]{new Range(1,2)});
    }

    /**
     * 
     */
    public void testSynchBeginSubset1()
    {
        Range a = new Range(0,1);
        Range b = new Range(0,3);
        shouldBe(a,b,new Range[]{new Range(0,1)},new Range[]{new Range(0,1),new Range(2,3)});
    }

    /**
     * 
     */
    public void testSynchBeginSubset2()
    {
        Range a = new Range(0,1);
        Range b = new Range(0,3);
        shouldBe(b,a,new Range[]{new Range(0,1),new Range(2,3)},new Range[]{new Range(0,1)});
    }

    /**
     * 
     */
    public void testSynchEndSubset1()
    {
        Range a = new Range(2,3);
        Range b = new Range(0,3);
        shouldBe(a,b,new Range[]{new Range(2,3)},new Range[]{new Range(0,1),new Range(2,3)});
    }

    /**
     * 
     */
    public void testSynchEndSubset2()
    {
        Range a = new Range(2,3);
        Range b = new Range(0,3);
        shouldBe(b,a,new Range[]{new Range(0,1),new Range(2,3)},new Range[]{new Range(2,3)});
    }

    /**
     * 
     */
    public void testEndToEnd1()
    {
        Range a = new Range(0,1);
        Range b = new Range(1,2);
        shouldBe(a,b,new Range[]{new Range(0,0),new Range(1,1)},new Range[]{new Range(1,1),new Range(2,2)});
    }

    /**
     * 
     */
    public void testEndToEnd2()
    {
        Range a = new Range(0,1);
        Range b = new Range(1,2);
        shouldBe(b,a,new Range[]{new Range(1,1),new Range(2,2)},new Range[]{new Range(0,0),new Range(1,1)});
    }

    /**
     * 
     */
    public void testSingle1()
    {
        Range a = new Range(1,1);
        Range b = new Range(1,1);
        shouldBe(a,b,new Range[]{new Range(1,1)},new Range[]{new Range(1,1)});
    }

    /**
     * 
     */
    public void testSingle2()
    {
        Range a = new Range(1,1);
        Range b = new Range(3,3);
        shouldBe(a,b,new Range[]{new Range(1,1)},new Range[]{new Range(3,3)});
    }

    /**
     * 
     */
    public void testSingle3()
    {
        Range a = new Range(1,3);
        Range b = new Range(1,1);
        shouldBe(a,b,new Range[]{new Range(1,1),new Range(2,3)},new Range[]{new Range(1,1)});
    }

    /**
     * 
     */
    public void testSingle4()
    {
        Range a = new Range(1,3);
        Range b = new Range(1,1);
        shouldBe(b,a,new Range[]{new Range(1,1)},new Range[]{new Range(1,1),new Range(2,3)});
    }

    /**
     * 
     */
    public void testSingle5()
    {
        Range a = new Range(1,3);
        Range b = new Range(3,3);
        shouldBe(a,b,new Range[]{new Range(1,2),new Range(3,3)},new Range[]{new Range(3,3)});
    }

    /**
     * 
     */
    public void testSingle6()
    {
        Range a = new Range(1,3);
        Range b = new Range(3,3);
        shouldBe(b,a,new Range[]{new Range(3,3)},new Range[]{new Range(1,2),new Range(3,3)});
    }

    /**
     * 
     */
    public void testSingle7()
    {
        Range a = new Range(1,3);
        Range b = new Range(2,2);
        shouldBe(a,b,new Range[]{new Range(1,1),new Range(2,2),new Range(3,3)},new Range[]{new Range(2,2)});
    }

    /**
     * 
     */
    public void testSingle8()
    {
        Range a = new Range(1,3);
        Range b = new Range(2,2);
        shouldBe(b,a,new Range[]{new Range(2,2)},new Range[]{new Range(1,1),new Range(2,2),new Range(3,3)});
    }

    /**
     * 
     */
    public void testSingle9()
    {
        Range a = new Range(1,2);
        Range b = new Range(1,1);
        shouldBe(a,b,new Range[]{new Range(1,1),new Range(2,2)},new Range[]{new Range(1,1)});
    }

    /**
     * 
     */
    public void testSingle10()
    {
        Range a = new Range(1,2);
        Range b = new Range(1,1);
        shouldBe(b,a,new Range[]{new Range(1,1)},new Range[]{new Range(1,1),new Range(2,2)});
    }

    /**
     * 
     */
    public void testSingle11()
    {
        Range a = new Range(1,2);
        Range b = new Range(2,2);
        shouldBe(a,b,new Range[]{new Range(1,1),new Range(2,2)},new Range[]{new Range(2,2)});
    }

    /**
     * 
     */
    public void testSingle12()
    {
        Range a = new Range(1,2);
        Range b = new Range(2,2);
        shouldBe(b,a,new Range[]{new Range(2,2)},new Range[]{new Range(1,1),new Range(2,2)});
    }

    /**
     * @param a
     * @param b
     * @param ranges
     */
    private void shouldBe(Range a, Range b, Range[] rangea, Range[] rangeb)
    {
        List ra = new ArrayList();
        List rb = new ArrayList();
        Range.chunk(a, b, ra, rb);

        assertEquals(rangea.length,ra.size());
        int n = 0;
        for (Iterator i = ra.iterator(); i.hasNext();)
        {
            Range range = (Range)i.next();
            assertEquals(rangea[n++],range);
        }

        assertEquals(rangeb.length,rb.size());
        n = 0;
        for (Iterator i = rb.iterator(); i.hasNext();)
        {
            Range range = (Range)i.next();
            assertEquals(rangeb[n++],range);
        }
}
}
