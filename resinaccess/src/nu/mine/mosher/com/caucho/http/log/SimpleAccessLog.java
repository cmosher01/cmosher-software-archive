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

    	System.err.println("--------------------> IP:"+request.getRemoteAddr()+
			", method:"+request.getMethod()+
			", URI:"+request.getRequestURI()+
			status+
			", browser:"+request.getHeader("User-Agent")+
			", referer:"+request.getHeader("Referer"));
    }
}
