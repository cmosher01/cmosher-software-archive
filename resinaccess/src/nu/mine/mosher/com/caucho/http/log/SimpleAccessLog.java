package nu.mine.mosher.com.caucho.http.log;

import java.io.IOException;

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
    	String status = "";
    	if (response instanceof AbstractHttpResponse)
    	{
    		status = ", status "+((AbstractHttpResponse)response).getStatusCode();
    	}
    	System.err.println("--------------------> IP:"+request.getRemoteAddr()+
			", method:"+request.getMethod()+
			", URI:"+request.getRequestURI()+
			status+
			", browser:"+request.getHeader("User-Agent")+
			", referer:"+request.getHeader("Referer"));
    }
}
