package solution;

import java.util.Random;

public class RandomNonceStrategy implements NonceStrategy
{
	private static Random random = new Random();
	public String generateNonce()
	{
		return Integer.toString(random.nextInt());
	}
}
