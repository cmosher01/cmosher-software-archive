import edu.stanford.cs.knuth.sa.random.RanArray;

/*
 * Created on Apr 24, 2005
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class HotBitsAndRanArray
{
	public static void main(String[] args)
	{
		// RanArray only uses low 32 bits of seed:
		RanArray ra = new RanArray(0x123456789abcdef0L);
		int r = ra.nextInt();
		r = ra.nextInt();
	}
}
