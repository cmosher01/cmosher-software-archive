import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * Created on Jan 18, 2005
 */


/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class HostsFile
{
    Map map = new HashMap();

    public void loadFromFile() throws IOException
    {
        String sf = "C:\\winnt\\system32\\drivers\\etc\\hosts";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sf))));
        for (String s = in.readLine(); in != null; s = in.readLine())
        {
            StringTokenizer st = new StringTokenizer(s);
            String ip = st.nextToken();
            while (st.hasMoreTokens())
            {
                String host = st.nextToken().toLowerCase();
                map.put(host, ip);
            }
        }
        in.close();
    }

    public String lookupHost(String host)
    {
        if (map.containsKey(host))
        {
            return (String)map.get(host);
        }
        return "";
    }
}
