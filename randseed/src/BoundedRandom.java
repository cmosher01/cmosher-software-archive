import java.util.Random;

/*
 * Created on Apr 24, 2005
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class BoundedRandom
{
	private static final int N = 20;
	private static final int TESTS = 2000000;
	private static int nr = 0;

	public static void main(String[] args)
	{
		Random rng = new Random()
		{
			public int nextInt(int n)
			{
				if ((n & -n) == n) // i.e., n is a power of 2
					return (int)((n * (long)next(31)) >> 31);
				int bits, val;
				do
				{
					bits = next(31);
					val = bits % n;
					if (bits - val + (n - 1) < 0)
					{
						System.out.println("met");
					}
				}
				while (bits - val + (n - 1) < 0);
				return val;
			}
			public int next(int bits)
			{
				return nr++;
			}
		};

		int[] rc = new int[N];
		for (int i = 0; i < rc.length; i++)
		{
			rc[i] = 0;
		}

		for (int test = 0; test < TESTS; ++test)
		{
			int rnd = rng.nextInt(N);
			++rc[rnd];
		}

		for (int i = 0; i < rc.length; i++)
		{
			int j = rc[i];
			System.out.println(i+": "+rc[i]);
		}
		System.out.println("nr: "+nr);
	}
}
