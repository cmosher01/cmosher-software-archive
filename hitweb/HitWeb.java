import java.net.HttpURLConnection;
import java.net.URL;

public class HitWeb
{
    public static void main(String[] args)
    {
    	HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection web = new HttpURLConnection(new URL("http://btndfopry.nm.ru/obr2.html?screen_width=2048&password=1234&x=43&y=14&cin=1234123412341234"));
    }
}
