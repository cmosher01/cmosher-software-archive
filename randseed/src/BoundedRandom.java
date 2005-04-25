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
	public static void main(String[] args)
	{
		int n = 3;
		for (int rand = 0; rand < 8; ++rand)
		{
	       int bits, val;
	        do {
	            bits = rand;
	            val = bits % n;
	            System.out.print("bits: "+bits);
	            System.out.print(" val: "+val);
	            System.out.print(" bits - val + (n-1): "+(bits - val + (n-1)));
	        } while(bits - val + (n-1) < 0);
		}
	}
}
