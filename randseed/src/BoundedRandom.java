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
	private static final int N = 5;
	private static final int TESTS = 100000;
	public static void main(String[] args)
	{
		Random rng = new Random();

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
	}
}
