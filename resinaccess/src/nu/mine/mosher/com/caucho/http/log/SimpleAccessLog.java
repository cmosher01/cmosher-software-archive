package nu.mine.mosher.com.caucho.http.log;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.http.log.AbstractAccessLog;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SimpleAccessLog extends AbstractAccessLog
{

    /**
     * 
     */
    public SimpleAccessLog()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.caucho.http.log.AbstractAccessLog#log(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.ServletContext)
     */
    public void log(HttpServletRequest arg0, HttpServletResponse arg1, ServletContext arg2) throws IOException
    {
        // TODO Auto-generated method stub

    }

}
