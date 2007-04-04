import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SimpleShowParameters extends HttpServlet
{
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException
    {
        showParameters(req,res);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException
    {
        showParameters(req,res);
    }

    public void showParameters(HttpServletRequest req, HttpServletResponse res) throws ServletException
    {
        StringBuffer msg = new StringBuffer(256);
        try
        {
            ServletContext ctx = this.getServletContext();
            PrintWriter out = res.getWriter();
            out.println("<html><head></head><body><table border=\"1\">");
            for (Enumeration e = ctx.getInitParameterNames(); e.hasMoreElements(); )
            {
                String n = (String)e.nextElement();
                String v = ctx.getInitParameter(n);
                msg.append(n);
                msg.append("=");
                msg.append(v);
                msg.append(";");
                StringBuffer s = new StringBuffer(256);
                s.append("<tr><td>");
                s.append(n);
                s.append("</td><td>");
                s.append(v);
                s.append("</td></tr>");
                out.println(s);
            }
            for (Enumeration e = ctx.getAttributeNames(); e.hasMoreElements(); )
            {
                String n = (String)e.nextElement();
                String v = ctx.getAttribute(n).toString();
                msg.append(n);
                msg.append("=");
                msg.append(v);
                msg.append(";");
                StringBuffer s = new StringBuffer(256);
                s.append("<tr><td>");
                s.append(n);
                s.append("</td><td>");
                s.append(v);
                s.append("</td></tr>");
                out.println(s);
            }
//            for (Enumeration e = req.getParameterNames(); e.hasMoreElements(); )
//            {
//                String n = (String)e.nextElement();
//                String v = req.getParameter(n);
//                msg.append(n);
//                msg.append("=");
//                msg.append(v);
//                msg.append(";");
//                StringBuffer s = new StringBuffer(256);
//                s.append("<tr><td>");
//                s.append(n);
//                s.append("</td><td>");
//                s.append(v);
//                s.append("</td></tr>");
//                out.println(s);
//            }
            out.println("</table></body></html>");
        }
        catch (Throwable e)
        {
            throw new ServletException(e);
        }
        finally
        {
            System.err.println(msg.toString());
        }
    }
}
