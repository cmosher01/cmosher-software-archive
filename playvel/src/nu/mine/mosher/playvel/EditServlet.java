package nu.mine.mosher.playvel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nu.mine.mosher.servlet.Servlets;
import nu.mine.mosher.velocity.VelocityWrapper;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public class EditServlet extends ServletDefault
{
	protected void processGetRequest(HttpServletRequest request, HttpServletResponse response, VelocityWrapper velocity, PersistenceManager pm) throws ServletException
	{
		try
		{
			String id = request.getParameter("id");
			boolean adding = (id == null || id.length() == 0);

			Object item;
			if (adding)
			{
                item = getItemNew();
			}
			else
			{
                item = getItemOld(pm,id);
			}

			List names = new ArrayList();
            getFieldNames(names);

			Map fields = new HashMap();
			Servlets.buildFieldMap(item,names,fields);

			Context ctx = new VelocityContext();
			ctx.put("fields",fields);

			velocity.mergeTemplate("edit.vm",ctx,response.getWriter());
		}
		catch (Throwable e)
		{
			throw new ServletException(e);
		}
	}

    protected void getFieldNames(List names)
    {
        names.add("id");
        names.add("name");
        names.add("attrib");
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
