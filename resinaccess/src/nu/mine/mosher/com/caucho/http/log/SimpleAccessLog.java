package nu.mine.mosher.com.caucho.http.log;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.http.log.AccessLog;

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
    }
}
