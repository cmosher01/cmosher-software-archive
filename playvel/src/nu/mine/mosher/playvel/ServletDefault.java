package nu.mine.mosher.playvel;

import java.io.IOException;
import java.util.Properties;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nu.mine.mosher.beans.editors.Editors;
import nu.mine.mosher.velocity.VelocityWrapper;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.VelocityException;

public abstract class ServletDefault extends HttpServlet
{
    protected void noGet(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        try
        {
            super.doGet(request, response);
        }
        catch (ServletException e)
        {
            throw e;
        }
        catch (Throwable e)
        {
            throw new ServletException(e);
        }
    }

    protected void noPost(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        try
        {
            super.doPost(request, response);
        }
        catch (ServletException e)
        {
            throw e;
        }
        catch (Throwable e)
        {
            throw new ServletException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        try
        {
            tryGet(request, response);
        }
        catch (ServletException e)
        {
            throw e;
        }
        catch (Throwable e)
        {
            throw new ServletException(e);
        }
    }

    protected void tryGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, VelocityException, ServletException
    {
        Editors.register();

        ServletContext ctx = getServletContext();
        response.setContentType("text/html; charset=UTF-8");

        String path = ctx.getRealPath("/");
        if (path == null)
        {
            throw new ServletException("Cannot get path of web application.");
        }

        Properties props = new Properties();
        VelocityWrapper.getDefaultProperties(props);
        props.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path);
        props.setProperty("file.resource.loader.modificationCheckInterval", "1");

        processGetRequest(request, response, new VelocityWrapper(props), new PermMgrFactory(ctx).getPM());
    }

    protected abstract void processGetRequest(
        HttpServletRequest request,
        HttpServletResponse response,
        VelocityWrapper velocity,
        PersistenceManager pm)
        throws ServletException;
}
