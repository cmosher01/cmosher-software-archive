package nu.mine.mosher.playvel;

import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nu.mine.mosher.velocity.VelocityWrapper;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public class DeleteServlet extends ServletDefault
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
			pm.currentTransaction().begin();

			PermItem item = getItemOld(pm,request.getParameter("id"));

			Context ctx = new VelocityContext();
			ctx.put("name",item.getName());

			pm.deletePersistent(item);

			pm.currentTransaction().commit();

			velocity.mergeTemplate("delete.vm",ctx,response.getWriter());
		}
		catch (Throwable e)
		{
			throw new ServletException(e);
		}
	}

	protected PermItem getItemOld(PersistenceManager pm, String id) throws JDOUserException
	{
		return Item.getById(pm,id);
	}
}
