import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * Created on Nov 10, 2005
 */
public class Dict
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("c:/dictionary/dict.txt"))));
		String lastISE = "";
		for (String s = in.readLine(); s != null; s = in.readLine())
		{
			if (s.endsWith("ise"))
			{
				lastISE = s;
			}
			else if (s.endsWith("ize"))
			{
				if (lastISE.length() < 3)
				{
					continue;
				}
				final String rootIZE = s.substring(0,s.length()-3);
				final String rootISE = lastISE.substring(0,lastISE.length()-3);
				if (rootIZE.equals(rootISE))
				{
					System.out.println(lastISE+"--"+s);
				}
			}
		}
		in.close();
	}
}
