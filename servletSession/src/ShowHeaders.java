import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ShowHeaders extends HttpServlet
{
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException
    {
        try
        {
            get(req,res);
        }
        catch (Throwable e)
        {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException
    {
        try
        {
            post(req,res);
        }
        catch (Throwable e)
        {
            throw new ServletException(e);
        }
    }

    protected void get(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        log("GET  request received-----------------------");

        final HttpSession ses = prepSession(req,res);
        if (ses == null)
        {
            return;
        }

        res.setContentType("text/html; charset=UTF-8");

        writeParameters(req,res);

        writeForm(req,res);

        final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(res.getOutputStream(),"UTF-8")),true);

        out.println("<table>");

        final Enumeration rNames = req.getHeaderNames();
        if (rNames == null || !rNames.hasMoreElements())
        {
            out.println("<tr><td>Cannot access headers.</td></tr>");
        }
        else
        {
            while (rNames.hasMoreElements())
            {
                final String en = (String)rNames.nextElement();
                writeHeaderBegin(out,en);
                final Enumeration rHeads = req.getHeaders(en);
                if (rHeads==null || !rHeads.hasMoreElements())
                {
                    writeHeaderError(out);
                }
                else
                {
                    while (rHeads.hasMoreElements())
                    {
                        String h = (String)rHeads.nextElement();
                        writeHeaderValue(out,h);
                    }
                }
                writeHeaderEnd(out);
            }
        }

        out.println("</table>");
    }




    public static HttpSession prepSession(final HttpServletRequest req, final HttpServletResponse res) throws IOException
    {
    	HttpSession ses = req.getSession();
        synchronized (ses)
        {
            if (ses.isNew())
            {
                /*
                 * If this is a newly created session,
                 * redirect the user back to this servlet
                 * to establish the rewritten URL (if any)
                 * in his browser. (Then continue below.)
                 * 
                 * Get rewritten URL that we can use to
                 * redirect the client back to us again.
                 * Note that for a new session, this may (will always?)
                 * include a JSESSIONID, even in the case where
                 * the server will be using cookies (instead of
                 * rewritten URLs) to maintain the session. The
                 * response may (will always?) also contain a
                 * set-cookie command with the JSESSIONID.
                 *
                 * In the case where the server cannot establish
                 * a context, this will result in an infinite
                 * redirect loop. The client's browser is
                 * responsible for breaking the loop and reporting
                 * to the user that disabled cookies may be the
                 * cause of the problem.
                 * 
                 * An example of the server not being able to
                 * establish a context would be where the client
                 * has cookies turned off, and the server doesn't
                 * use URL rewriting or any other means to establish
                 * contexts. The solutions are: require that the
                 * user turn cookies on, or use a server that
                 * supports URL rewriting.
                 */
                res.sendRedirect(getRedirectToSelf(req,res));
                ses = null;
            }
            else if (!isStarted(ses))
            {
                /*
                 * If this is the first redirect (from above),
                 * mark our session as "started" so we don't
                 * come in here next time.
                 */
                setStarted(ses);

                /*
                 * If the redirect was from a rewritten URL,
                 * do nothing because the user's browser has
                 * the correct URL, otherwise, redirect them
                 * back to this servlet to establish the
                 * unadorned URL in their browser.
                 */
                if (!req.isRequestedSessionIdFromURL())
                {
                    res.sendRedirect(getRedirectToSelf(req,res));
                    ses = null;
                }
            }
            /*
             * Otherwise, we're good to go, so return
             * the session to our caller.
             */
        }
        return ses;
    }

    private static final String STARTED = "D7936D9C-60E5-10FA-E034-080020B182C1";

    private static void setStarted(final HttpSession ses)
    {
        ses.setAttribute(STARTED,new Object());
    }

    private static boolean isStarted(final HttpSession ses)
    {
        return (ses.getAttribute(STARTED) != null);
    }

    private static String getRedirectToSelf(final HttpServletRequest req, final HttpServletResponse res)
    {
        return res.encodeRedirectURL(getRequestURIWithQueryString(req));
    }

    private static String getLinkToSelf(final HttpServletRequest req, final HttpServletResponse res)
    {
        return res.encodeURL(getRequestURIWithQueryString(req));
    }



    public static String getRequestURIWithQueryString(final HttpServletRequest req)
    {
    	final StringBuilder str = new StringBuilder();

    	str.append(req.getRequestURI());

        final String q = req.getQueryString();
        if (q != null && q.length() > 0)
        {
            str.append("?");
            str.append(q);
        }

        return str.toString();
    }













    protected void writeForm(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        String rd = getLinkToSelf(req,res);
        PrintWriter out = res.getWriter();
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        out.println("<title>Test posting</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<form method=\"post\" action=\""+rd+"\">");
        out.println("<input type=\"text\"   name=\"f\"      id=\"f\"      value=\"abc\"    />");
        out.println("<input type=\"submit\" name=\"OK\"     id=\"OK\"     value=\"OK\"     />");
        out.println("<input type=\"submit\" name=\"Cancel\" id=\"Cancel\" value=\"Cancel\" />");
        out.println("</form>");
        out.println("<p><a href=\""+rd+"\">"+rd+"</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
    protected void writeBegin(PrintWriter out, String title)
    {
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        out.print("<title>");
        out.print(title);
        out.println("</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<table border=\"1\">");
    }

    protected void writeEnd(PrintWriter out)
    {
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    protected void writeHeaderBegin(PrintWriter out, String en)
    {
        out.print("<tr><td nowrap=\"nowrap\">");
        out.print(en);
        out.print("</td><td>");
    }

    protected void writeHeaderEnd(PrintWriter out)
    {
        out.println("</td></tr>");
    }

    protected void writeHeaderError(PrintWriter out)
    {
        out.print("[null]");
    }

    protected void writeHeaderValue(PrintWriter out, String val)
    {
        out.print(val);
        out.print("<br />");
    }

    protected void post(final HttpServletRequest req, final HttpServletResponse res) throws InterruptedException, IOException
    {
        log("POST request received-----------------------");

        final HttpSession ses = req.getSession(false);
        if (ses == null)
        {
            log("    session doesn't exist");
            return;
        }
        synchronized (ses)
        {
            log(ses.getId());
            Thread.sleep(2000);

            final Object sub = ses.getAttribute("submitted");
            if (sub == null)
            {
                ses.setAttribute("submitted",new Object());
                log("    POST request PROCESSING ");
            }
            else
            {
                log("    POST request ignoring ");
            }

            res.setContentType("text/html; charset=UTF-8");

            writeParameters(req,res);
        }
    }

    protected void writeParameters(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        PrintWriter out = res.getWriter();
        
        writeBegin(out,"Request Parameters");
        
        Enumeration rNames = req.getParameterNames();
        if (rNames==null || !rNames.hasMoreElements())
        {
            out.println("<tr><td>No parameters found.</td></tr>");
        }
        else
        {
            while (rNames.hasMoreElements())
            {
                String en = (String)rNames.nextElement();
                writeHeaderBegin(out,en);
                String[] rHeads = req.getParameterValues(en);
                if (rHeads==null || rHeads.length==0)
                {
                    writeHeaderError(out);
                }
                else
                {
                    for (int i = 0; i < rHeads.length; ++i)
                    {
                        String h = rHeads[i];
                        writeHeaderValue(out,h);
                    }
                }
                writeHeaderEnd(out);
            }
        }
        
        writeEnd(out);
    }
}
