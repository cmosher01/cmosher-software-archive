package nu.mine.mosher.playvel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nu.mine.mosher.servlet.Servlets;
import nu.mine.mosher.velocity.VelocityWrapper;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public class EditActServlet extends ServletDefault
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
			String page;
			Context ctx = new VelocityContext();

			boolean okCommit = false;
			try
			{
				pm.currentTransaction().begin();

				Map params = new HashMap(request.getParameterMap());
				Set keep = new HashSet();
				keep.add("name");
				keep.add("attrib");
				params.keySet().retainAll(keep);

				String id = request.getParameter("id");
				Object item;
				if (id != null && id.length() > 0)
				{
					item = getItemOld(pm,id);
				}
				else
				{
					item = getItemNew();
					pm.makePersistent(item);
					id = "";
				}
				ctx.put("id",id);

				List rfieldResults = new ArrayList();
				Servlets.parseParameters(item,params,rfieldResults);

				boolean badField = false;
				Map fields = new HashMap();
				for (Iterator i = rfieldResults.iterator(); i.hasNext();)
	            {
	                FormField ff = (FormField)i.next();
					fields.put(ff.getName(),ff);
	                if (ff.isBad())
	                {
						badField = true;
	                }
	            }
				ctx.put("fields",fields);

				if (!badField)
				{
					okCommit = true;
				}
			}
			finally
			{
				if (okCommit)
				{
					pm.currentTransaction().commit();
					page = "editact";
				}
				else
				{
					pm.currentTransaction().rollback();
					page = "edit";
				}
			}
			velocity.mergeTemplate(page+".vm",ctx,response.getWriter());
		}
		catch (Throwable e)
		{
			throw new ServletException(e);
		}
	}

	protected Object getItemOld(PersistenceManager pm, String id) throws JDOUserException
	{
		return Item.getById(pm,id);
	}

	protected Object getItemNew()
	{
		return new Item();
	}
}
