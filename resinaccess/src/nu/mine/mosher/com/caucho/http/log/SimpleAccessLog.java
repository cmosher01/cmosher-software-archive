package nu.mine.mosher.com.caucho.http.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.http.log.AccessLog;
import com.caucho.server.connection.AbstractHttpResponse;

/**
 * @author Chris Mosher
 */
public class SimpleAccessLog extends AccessLog
{
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmssSSS");
	private static final SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd");
    public SimpleAccessLog()
    {
        super();
    }

    public void log(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws IOException
    {
    	Date ts = new Date();

    	super.log(request,response,context);

    	int status = -1;
    	if (response instanceof AbstractHttpResponse)
    	{
    		status = ((AbstractHttpResponse)response).getStatusCode();
    	}
//    	boolean good = (status==HttpServletResponse.SC_OK || status==HttpServletResponse.SC_NOT_MODIFIED);

		Inet4Address ip = (Inet4Address)InetAddress.getByName(request.getRemoteAddr());
		String method = request.getMethod();
		String uri = request.getRequestURI();
		String query = request.getQueryString();
		String browser = request.getHeader("User-Agent");
		if (browser == null)
		{
			browser = "";
		}
		String referer = request.getHeader("Referer");
		if (referer == null)
		{
			referer = "";
		}

		if (ip.isSiteLocalAddress() || ip.isLoopbackAddress())
		{
			return;
		}

		StringBuffer sb = new StringBuffer(256);
//    	sb.append("--------------------> ");

		sb.append(format.format(ts));
		sb.append(",");
		sb.append(ip.getHostAddress());
		sb.append(",");
		sb.append(ip.getHostName());
		sb.append(",");
		sb.append(method);
		sb.append(",");
		sb.append(uri);
		sb.append(",");
		sb.append(query);
		sb.append(",");
		sb.append(browser);
		sb.append(",");
		sb.append(referer.startsWith("http://mosher.mine.nu")?"":referer);
		sb.append(",");
//		sb.append(!good?Integer.toString(status):"");
		sb.append(status);

		BufferedWriter accesslog = null;
		try
		{
			accesslog = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("c:\\temp\\access"+form.format(ts)),true)));
			accesslog.write(sb.toString());
			accesslog.newLine();
		}
		finally
		{
			if (accesslog != null)
			{
				accesslog.close();
			}
		}
    }
}
