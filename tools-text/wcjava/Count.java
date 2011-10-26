package nu.mine.mosher;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Counts lines in a text file.
 */
public class Count
{
	public static void main(String[] args) throws IOException
	{
		if (args.length > 0)
		{
			System.setIn(new FileInputStream(args[0]));
		}
		BufferedReader bin = new BufferedReader(new InputStreamReader(System.in));
		int c = 0;
		String s = bin.readLine();
		while (s != null)
		{
			++c; // prefix is inherently simpler than postfix
			s = bin.readLine();
		}
		System.out.println(c);
	}
	/* main trunk development */
}
