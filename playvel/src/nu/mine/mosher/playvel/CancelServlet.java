package nu.mine.mosher.playvel;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nu.mine.mosher.velocity.VelocityWrapper;

import org.apache.velocity.VelocityContext;

public class CancelServlet extends ServletDefault
{
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		super.noGet(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		super.doGet(request,response);
	}

	protected void processGetRequest(HttpServletRequest request, HttpServletResponse response, VelocityWrapper velocity, PersistenceManager pm) throws ServletException
	{
		try
		{
			velocity.mergeTemplate("cancel.vm",new VelocityContext(),response.getWriter());
		}
		catch (Throwable e)
		{
			throw new ServletException(e);
		}
	}
}
