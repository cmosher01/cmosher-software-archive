import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HitWeb
{
	private static final int TIMES = 10;
    public static void main(String[] args) throws Throwable
    {
    	HttpURLConnection.setFollowRedirects(false);
    	for (int i = 0; i < TIMES; ++i)
    	{
			URL url = new URL("http://btndfopry.nm.ru/obr2.html?screen_width=2048&password=1234&x=43&y=14&cin=1234123412341234");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setUseCaches(false);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			for (String s = in.readLine(); s != null; s = in.readLine())
			{
				System.oiut.println(s);
			}
			con.disconnect();
    	}
    }
}
