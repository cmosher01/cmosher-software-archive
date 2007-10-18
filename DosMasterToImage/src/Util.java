import java.io.IOException;
import java.io.OutputStream;

/*
 * Created on Oct 17, 2007
 */
public final class Util
{
	private Util() { throw new IllegalStateException(); }

	public static void wordout(int word, OutputStream out) throws IOException
	{
		out.write(word);
		out.write(word >> 8);
	}

	public static void nout(int n, int byt, OutputStream out) throws IOException
	{
		for (int i = 0; i < n; ++i)
		{
	    	out.write(byt);
		}
	}

	
}
