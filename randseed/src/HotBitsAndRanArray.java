/*
 * Created on April 24, 2005
 */

import edu.stanford.cs.knuth.sa.random.RanArray;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class HotBitsAndRanArray
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		RanArray ra = new RanArray(0x12345678Fabcdef0L);
		int r = ra.nextInt();
		r = ra.nextInt();
	}
}
