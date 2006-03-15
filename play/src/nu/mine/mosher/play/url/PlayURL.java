package nu.mine.mosher.play.url;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Created on Oct 28, 2005
 */
public class PlayURL
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		final URL url = new URL("file:///c:/temp/test.txt");
		System.out.println(url);
		final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
		for (String s = in.readLine(); s != null; s = in.readLine())
		{
			System.out.println(s);
		}

		final URL url2 = new URL(url,"junk.txt");
		System.out.println(url2);
		final BufferedReader in2 = new BufferedReader(new InputStreamReader(url2.openStream(),"UTF-8"));
		for (String s = in2.readLine(); s != null; s = in2.readLine())
		{
			System.out.println(s);
		}

		final URL res = PlayURL.class.getResource("test.res");
		System.out.println(res);
		final BufferedReader in3 = new BufferedReader(new InputStreamReader(res.openStream(),"UTF-8"));
		for (String s = in3.readLine(); s != null; s = in3.readLine())
		{
			System.out.println(s);
		}

		final URL res2 = new URL(res,"../url/sub/another.res");
		System.out.println(res2);
		final BufferedReader in4 = new BufferedReader(new InputStreamReader(res2.openStream(),"UTF-8"));
		for (String s = in4.readLine(); s != null; s = in4.readLine())
		{
			System.out.println(s);
		}
	}
}
