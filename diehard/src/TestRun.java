import edu.fsu.stat.geo.diehard.tests.RandomnessTest;
import edu.fsu.stat.geo.diehard.tests.Squeeze;
import edu.stanford.cs.knuth.sa.random.RanArray;

/**
 */
public class TestRun
{
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        RandomnessTest s = new Squeeze(new RanArray());

        double p = s.test();
        System.out.println(p);
    }
}
