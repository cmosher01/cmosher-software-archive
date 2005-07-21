/*
 * Created on July 18, 2005
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A small servlet.
 *
 * @author Chris Mosher
 */
public class Small extends HttpServlet
{
	private static final boolean AUTO_FLUSH = true;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException
	{
	    final String browser = request.getHeader("User-Agent");
	    final boolean standardsCompliantBrowser = (browser.indexOf("MSIE") == -1);

	    final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),"UTF-8")),AUTO_FLUSH);

	    if (standardsCompliantBrowser)
	    {
	        response.setContentType("application/xhtml+xml");
	        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
	    }
	    else
	    {
	        response.setContentType("text/html; charset=UTF-8");
	    }
	    out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
	    out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
	    out.println("<head></head>");
	    out.println("<body>");
	    out.println("Small servlet.");
	    out.println("</body>");
	    out.println("</html>");
	}
}
