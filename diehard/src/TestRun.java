import nu.mine.mosher.random.*;

import edu.fsu.stat.geo.diehard.tests.*;
import edu.fsu.stat.geo.random.*;

import edu.stanford.cs.knuth.sa.random.RanArray;

/**
 */
public class TestRun
{
    public static void main(String[] args) throws Exception
    {
        RandomnessTest s = new Squeeze(new RanArray());

        double p = s.test();
        System.out.println(p);
    }
}
