package nu.mine.mosher.playvel;

import java.util.ArrayList;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nu.mine.mosher.velocity.VelocityWrapper;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public class ListServlet extends ServletDefault
{
    protected void processGetRequest(HttpServletRequest request, HttpServletResponse response, VelocityWrapper velocity, PersistenceManager pm) throws ServletException
    {
		try
        {
			Context ctx = new VelocityContext();
			ctx.put("items",getItems(pm));

            velocity.mergeTemplate("list.vm",ctx,response.getWriter());
        }
        catch (Throwable e)
        {
        	throw new ServletException(e);
        }
    }

    protected ArrayList getItems(PersistenceManager pm)
    {
        return new Item().getAll(pm);
    }
}
