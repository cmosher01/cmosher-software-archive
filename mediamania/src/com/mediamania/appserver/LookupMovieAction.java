package com.mediamania.appserver;

import java.util.Collection;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.swing.Action;

import com.mediamania.content.Movie;

public class LookupMovieAction extends Action {
    PersistenceManagerFactory pmf = null;
    PersistenceManager pm = null;
    public ActionForward execute(ActionMapping mapping,
        ActionForm form,
        HttpServletRequest request,
        HttpServletResponse response)
        throws Exception {
        try {
            ServletContext ctx = getServlet().getServletContext();
            pmf = (PersistenceManagerFactory)ctx.getAttribute("jdo.Movies");
            pm = pmf.getPersistenceManager();
            Query q = pm.newQuery(Movie.class, "title == param1");
            q.declareParameters ("String param1");
            String movieName = request.getParameter("movieName");
            Collection movies = (Collection)q.execute(movieName);
            Movie movie = (Movie)movies.iterator().next();
            String description = movie.getDescription();
            ctx.setAttribute("movieDescription", description);
        } catch (JDOException e) {
        } finally {
            if (pm != null) {
                pm.close();
            }
            pm = null;
        }
        return (mapping.findForward("success"));
    }
}