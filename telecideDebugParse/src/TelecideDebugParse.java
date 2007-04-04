import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * Created on Feb 14, 2006
 */
public class TelecideDebugParse
{
	private static final String FRAME = "Telecide: frame ";
	private static final int LEN_FRAME = FRAME.length();
	private static final String USING = "[using ";
	private static final int LEN_USING = USING.length();

	private static final int MOD = 19;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			throw new IllegalArgumentException("usage: java TelecideDebugParse debugViewFile");
		}

		int frameExpected = 0;

		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
		for (String line = in.readLine(); line != null; line = in.readLine())
		{
			int posFrame = line.indexOf(FRAME);
			if (posFrame < 0)
			{
				continue;
			}
			posFrame += LEN_FRAME;


			int posUsing = line.indexOf(USING,posFrame);
			if (posUsing < 0)
			{
				continue;
			}
			posUsing += LEN_USING;

			final int posColon = line.indexOf(":",posFrame);
			final String sFrame = line.substring(posFrame,posColon);
			final int frame = Integer.parseInt(sFrame);

			if (frame != frameExpected)
			{
				throw new IllegalStateException("expected frame "+frameExpected+" but got frame "+frame);
			}

			String fieldMatched = line.substring(posUsing,posUsing+1);
//			if (fieldMatched.charAt(0) != 'c')
//			{
//				fieldMatched = fieldMatched.toUpperCase();
//			}
			if (frameExpected % MOD == 0)
			{
				System.out.printf("%06d,%06d ",frame,frame+MOD-1);
			}
			System.out.print(fieldMatched);

			++frameExpected;
			if (frameExpected % MOD == 0)
			{
				System.out.println();
			}
		}
		System.out.println();
		in.close();
	}
}
