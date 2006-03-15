/*
 * Created on July 18, 2005
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nu.mine.mosher.template.Templat;
import nu.mine.mosher.template.exception.TemplateLexingException;
import nu.mine.mosher.template.exception.TemplateParsingException;

/**
 * A small servlet.
 *
 * @author Chris Mosher
 */
public class Small extends HttpServlet
{
	private static final boolean AUTO_FLUSH = true;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException
	{
		try
		{
			tryGet(request,response);
		}
		catch (final Throwable e)
		{
			throw new ServletException(e);
		}
	}

	private void tryGet(final HttpServletRequest request, final HttpServletResponse response) throws TemplateLexingException, TemplateParsingException, IOException
	{
	    final StringBuilder bufPage = new StringBuilder();
	    getPage(request,bufPage);

	    final PrintWriter out = getOutput(request,response);
	    out.println(bufPage.toString());
	}

	private void getPage(final HttpServletRequest request, final StringBuilder bufPage) throws TemplateLexingException, TemplateParsingException, IOException
	{
		final URL urlSmallTemplate = this.getClass().getResource("small.tat");

		final Templat page = new Templat(urlSmallTemplate);

	    final Collection<Header> rHeader = new ArrayList<Header>();
	    getHeaders(request,rHeader);
	    page.addArg(rHeader);

	    page.parse(bufPage);
	}

	/**
	 * @param request 
	 * @param response
	 * @return response writer
	 * @throws RuntimeException
	 */
	public static PrintWriter getOutput(final HttpServletRequest request, final HttpServletResponse response)
	{
		final boolean isCompliant = checkBrowserCompliance(request);
		if (isCompliant)
		{
			response.setContentType("application/xhtml+xml; charset=UTF-8");
		}
		else
		{
			response.setContentType("text/html; charset=UTF-8");
		}

		try
		{
			final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),"UTF-8")),AUTO_FLUSH);
			if (isCompliant)
			{
				out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			}
			return out;
		}
		catch (final IOException e)
		{
			// Hmmm... no way to respond to the user?
			// I guess we really can't do too much, then.
			// So just call it a programming error.
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	private static boolean checkBrowserCompliance(final HttpServletRequest request)
	{
		final HTTPHeaderParser headerParser = new HTTPHeaderParser(request,"accept");
		return headerParser.contains("application/xhtml+xml");
	}

	public static class Header
	{
		private final String name;
		private final String value;
		private Header(final String name, final String value) { this.name = name; this.value = value; }
		public String getName() { return this.name; }
		public String getValue() { return this.value; }
	}

	private void getHeaders(final HttpServletRequest request, final Collection<Header> rHeader)
	{
		final Enumeration headerNames = request.getHeaderNames();
	    while (headerNames.hasMoreElements())
	    {
	    	final String headerName = (String)headerNames.nextElement();
	    	final Enumeration headers = request.getHeaders(headerName);
	    	while (headers.hasMoreElements())
	    	{
	    		final String headerValue = (String)headers.nextElement();
	    		rHeader.add(new Header(headerName,headerValue));
	    	}
	    }
	}
}
