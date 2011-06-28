import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/*
 * Created on Feb 24, 2006
 */
public class TelecideOvr
{
	private static final int LEN = 30;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			throw new IllegalArgumentException("usage: java TelecideOvr ovrFile");
		}

		final List<Character> rOvr = new ArrayList<Character>(); 

		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
		for (String line = in.readLine(); line != null; line = in.readLine())
		{
			final StringTokenizer st = new StringTokenizer(line," ,");
			final int first = Integer.parseInt(st.nextToken());
			final int last = Integer.parseInt(st.nextToken());
			final char[] rFrame = st.nextToken().toCharArray();

			for (int iFrame = 0; iFrame <= last-first; ++iFrame)
			{
				final char ovr = rFrame[iFrame%rFrame.length];
				rOvr.add(ovr);
			}
		}
		in.close();

		int i = 0;
		for (final Character ovr: rOvr)
		{
			if (i % LEN == 0)
			{
				System.out.printf("\n%04d,%04d ",i,i+LEN-1);
			}

			System.out.print(ovr);

			++i;
		}
		System.out.println();
	}
}
