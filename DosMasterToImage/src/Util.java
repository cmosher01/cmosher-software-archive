import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

/*
 * Created on 2007-10-17
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

	public static void arrayout(final int[] r, final OutputStream out) throws IOException
	{
		for (int i = 0; i < r.length; ++i)
		{
			out.write(r[i]);
		}
	}



	public static String nextTok(final StringTokenizer tok)
	{
		if (!tok.hasMoreTokens())
		{
			return "";
		}
		return tok.nextToken();
	}
}
