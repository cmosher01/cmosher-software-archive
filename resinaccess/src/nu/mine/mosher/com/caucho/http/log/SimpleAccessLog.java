package nu.mine.mosher.com.caucho.http.log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

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
    public SimpleAccessLog()
    {
        super();
    }

    public void log(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws IOException
    {
    	super.log(request,response,context);

    	int status = -1;
    	if (response instanceof AbstractHttpResponse)
    	{
    		status = ((AbstractHttpResponse)response).getStatusCode();
    	}

		Inet4Address ip = (Inet4Address)InetAddress.getByName(request.getRemoteAddr());
		String method = request.getMethod();
		String uri = request.getRequestURI();
		String browser = request.getHeader("User-Agent");
		String referer = request.getHeader("Referer");

		if (ip.isSiteLocalAddress() || ip.isLinkLocalAddress())
		{
			return;
		}

		StringBuffer sb = new StringBuffer(256);
    	sb.append("--------------------> ");

		sb.append(ip.getHostAddress());
		sb.append(",");
		sb.append(ip.getHostName());
		sb.append(",");
		sb.append(method);
		sb.append(",");
		sb.append(uri);
		sb.append(",");
		sb.append(browser);
		sb.append(",");
		sb.append(referer.startsWith("http://mosher.mine.nu")?"":referer);
		sb.append(",");
		sb.append(status!=HttpServletResponse.SC_OK?Integer.toString(status):"");

    }
}
