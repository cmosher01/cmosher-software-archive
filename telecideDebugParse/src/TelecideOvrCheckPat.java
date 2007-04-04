import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/*
 * Created on Feb 18, 2006
 */
public class TelecideOvrCheckPat
{
	private final static char[] PAT = "pcnpnpcnpcnpcnpnpcn".toCharArray();

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

		int iPat = 0;
		boolean startline = true;


		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
		for (String line = in.readLine(); line != null; line = in.readLine())
		{
			final StringTokenizer st = new StringTokenizer(line," ,");
			final int first = Integer.parseInt(st.nextToken());
			final int last = Integer.parseInt(st.nextToken());
			final char[] rFrame = st.nextToken().toCharArray();

			for (int iFrame = 0; iFrame <= last-first; ++iFrame)
			{
				final char frame = rFrame[iFrame%rFrame.length];

				if (startline)
				{
					System.out.printf("%04d ",first+iFrame);
					startline = false;
				}
				System.out.print(frame);

//				System.out.printf("%04d ",first+iFrame);
//				System.out.print(frame);
//				System.out.print(' ');
//				System.out.print(PAT[iPat%PAT.length]);

				if (frame != PAT[iPat%PAT.length])
				{
//					System.out.print(" ******");
					if (first+iFrame==1923)
					{
//						System.out.print(" EDITED ON VIDEO");
						System.out.println();
						startline = true;
						iPat += 17;
					}
					else if (PAT[iPat%PAT.length]=='n' && frame=='c')
					{
//						System.out.print(" OK");
						System.out.println();
						startline = true;
						iPat += 8;
					}
				}
				++iPat;

//				System.out.println();
			}
		}
		System.out.println();
	}
}
