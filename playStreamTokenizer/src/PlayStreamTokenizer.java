import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class PlayStreamTokenizer
{

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException
	{
		if (args.length != 1)
		{
			System.out.println("usage: java program database");
			return;
		}

		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)));
		final SqlTokenizer tok = new SqlTokenizer(in);

		for (String s = tok.nextToken(); !s.isEmpty(); s = tok.nextToken())
		{
			System.out.print(s+" ");
		}
		System.out.flush();
		in.close();
	}

}
