package com.mediamania.appserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.jdo.Extent;
import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.mediamania.content.Movie;

public class MovieInfo extends HttpServlet {
    PersistenceManagerFactory persistenceManagerFactory;
    PersistenceManager pm;
    public void init() throws ServletException {
        try {
            ServletContext ctx = getServletContext();
            InputStream in = ctx.getResourceAsStream("WEB-INF/pmf.properties");
            Properties props = new Properties();
            props.load(in);
            persistenceManagerFactory = 
                JDOHelper.getPersistenceManagerFactory(props);
        } catch (IOException ex) {
            throw new ServletException("Unable to load PMF properties.", ex);
        } catch (JDOException ex) {
            throw new ServletException("Unable to create PMF resource.", ex);
        } catch (Exception ex) {
            throw new ServletException("Unable to initialize.", ex);
        }
        
    }
            
    /**
       Destroys the servlet.
     */
    public void destroy() {
    }
    
    /** Processes requests for both HTTP <code>GET</code>
     * and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, 
        HttpServletResponse response)
            throws ServletException, java.io.IOException {
        pm = persistenceManagerFactory.getPersistenceManager();
        response.setContentType("text/html");
        java.io.PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.print(formatMovieInfo());
        out.println("</body>");
        out.println("</html>");
        out.close();
        pm.close();
    }
    
    protected String formatMovieInfo() {
        StringBuffer result = new StringBuffer();
        Extent movies = pm.getExtent(Movie.class, true);
        Iterator it = movies.iterator();
        while (it.hasNext()) {
            result.append("<P>");
            Movie movie = (Movie)it.next();
            result.append(movie.getDescription());
        }
        return result.toString();
    }
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request,
        HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, 
        HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Movie Information";
    }
    
}