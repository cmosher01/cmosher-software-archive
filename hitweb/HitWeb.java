import java.net.HttpURLConnection;
import java.net.URL;

public class HitWeb
{
    public static void main(String[] args) throws Throwable
    {
    	HttpURLConnection.setFollowRedirects(false);
		URL url = new URL("http://btndfopry.nm.ru/obr2.html?screen_width=2048&password=1234&x=43&y=14&cin=1234123412341234");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		String s = (String)con.getContent();
		System.out.println(s);
		con.disconnect();
    }
}
