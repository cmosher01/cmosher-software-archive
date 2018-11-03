package com.surveysampling.testwarjee6;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DumpHeaders extends HttpServlet
{
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			tryGet(req,resp);
		}
		catch (final Throwable e)
		{
			if (isConnectionClosedException(e))
			{
				System.err.println("Detected connection closed");
				BufferedWriter err = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.err)));
				writeRequestInfo(req,err);
				err.flush();
			}
			else
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Checks if the given exception indicates that the client connection
	 * was closed. This is not a perfect test, but is fail-safe in that it
	 * will err on the side of returning <code>false</code> if the test cannot determine
	 * reliably if the connection was closed.
	 * 
	 * This test is currently implemented to determine "broken pipe" or
	 * "connection reset" and only on a tomcat (or jboss) container.
	 * @param e
	 * @return
	 */
	private static boolean isConnectionClosedException(final Throwable e)
	{
		final String exceptionClassName = e.getClass().getSimpleName();
		if (exceptionClassName == null)
		{
			return false;
		}

		/*
		 * This is the name of the class of exception that tomcat throws
		 * when it determines that the client aborted the connection.
		 * We don't check for "instance of" here, so we don't have to compile
		 * against the tomcat libraries.
		 */
		if (!exceptionClassName.equals("ClientAbortException"))
		{
			return false;
		}

		/*
		 * ClientAbortException (currently) uses its own mechanism to wrap the "caused by"
		 * exception, instead of the mechanism implemented by Throwable, so we cannot use
		 * "getCause" to get the underlying exception. Neither can we use "getMessage"
		 * because ClientAbortException uses its own mechanism to hold the message. So
		 * instead, we need use ClientAbortException.toString to get the message.
		 */
		final String msg = e.toString();
		if (msg == null)
		{
			return false;
		}

		/*
		 * Now do a case-insensitive check inside the message for the currently known
		 * cases of connection closing.
		 */
		final String lowerMsg = msg.toLowerCase();
		if (lowerMsg.contains("broken pipe") || lowerMsg.contains("connection reset"))
		{
			return true;
		}

		return false;
	}

	private void tryGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
	{
		System.out.println("Hit");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final BufferedWriter out = getOut(resp);

		writeRequestInfo(req, out);

		out.flush();
		System.out.println("Flushed");
	}

	/**
	 * @param req
	 * @param out
	 * @throws IOException
	 */
	private static void writeRequestInfo(final HttpServletRequest req, final BufferedWriter out) throws IOException
	{
		out.write("-------------HEADERS-------------------------------");
		out.newLine();

		final Enumeration headerNames = req.getHeaderNames();
		while (headerNames.hasMoreElements())
		{
			final String headerName = headerNames.nextElement().toString();
			out.write(headerName.toString());
			out.newLine();
			final Enumeration headers = req.getHeaders(headerName);
			while (headers.hasMoreElements())
			{
				final Object header = headers.nextElement();
				out.write("    ");
				out.write(header.toString());
				out.newLine();
			}
		}

		out.write("-------------COOKIES-------------------------------");
		out.newLine();

		final Cookie[] cookies = req.getCookies();
		if (cookies != null)
		{
			for (final Cookie cookie : cookies)
			{
				out.write(cookie.getName()+": "+cookie.getValue());
				out.newLine();
			}
		}

		out.write("-------------OTHER---------------------------------");
		out.newLine();

		out.write("request.getServerName: "+req.getServerName());
		out.newLine();

		out.write("request.getRemoteAddr: "+req.getRemoteAddr());
		out.newLine();

		out.write("request.getRemoteHost: "+req.getRemoteHost());
		out.newLine();

		out.write("request.getLocalAddr: "+req.getLocalAddr());
		out.newLine();

		out.write("request.getLocalName: "+req.getLocalName());
		out.newLine();

		out.write("request.getContextPath: "+req.getContextPath());
		out.newLine();

		out.write("-------------QUERY STRING--------------------------");
		out.newLine();

		final String queryString = req.getQueryString();
		out.write(queryString==null?"[null]":queryString);
		out.newLine();

		out.write("-------------PARAMETERS----------------------------");
		out.newLine();

		final Map<String,String[]> mapParams = req.getParameterMap();
		for (final Map.Entry<String,String[]> param : mapParams.entrySet())
		{
			out.write(param.getKey());
			out.newLine();
			for (final String value : param.getValue())
			{
				out.write("    ");
				out.write(value);
				out.newLine();
			}
		}
	}
	private BufferedWriter getOut(final HttpServletResponse resp) throws ServletException
	{
		try
		{
			return new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()));
		}
		catch (IOException e)
		{
			throw new ServletException(e);
		}
	}
}
