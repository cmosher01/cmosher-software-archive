import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.WeakHashMap;

public class WebCache
{
    WeakHashMap cache = new WeakHashMap();

    public class WebObject
    {
        public String type;
        public byte[] content;
    }



    public WebObject get(String spec) throws MalformedURLException, IOException
    {
        URL url = new URL(spec);
        WebObject obj = (WebObject) cache.get(url);
        if (obj == null)
        {
            URLConnection conn = url.openConnection();
            obj = new WebObject();
            obj.type = conn.getContentType();
            obj.content = new byte[conn.getContentLength()];
            conn.getInputStream().read(obj.content);
            cache.put(url, obj);
        }
        return obj;
    }
}
