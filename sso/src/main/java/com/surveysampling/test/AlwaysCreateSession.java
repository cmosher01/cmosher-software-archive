package com.surveysampling.test;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;


/**
 * Created by cmosher on 12/8/14.
 */
@WebServlet(urlPatterns = {"/alwaysCreateSession"})
public class AlwaysCreateSession extends HttpServlet {

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        invalidateAnyExistingSession(request);
        final HttpSession session = createSession(request);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;
        try {
            out = buildResponseWriter(response);
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"en\">");
            out.println("<head>");
            out.println("<meta charset=\"utf-8\">");
            out.println("<title>Always Create Session</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>OK, created session with ID: ");
            out.println(session.getId());
            out.println("</p>");
            out.println("</body>");
            out.println("</html>");
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private HttpSession createSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        while (session == null) {
            System.err.println("GETSESSION RETURNED NULL!!!!!!!!!!");
            System.err.flush();
            session = request.getSession();
        }
        return session;
    }

    private static void invalidateAnyExistingSession(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            /* session does not exist */
            return;
        }

        session.invalidate();
    }

    private static PrintWriter buildResponseWriter(HttpServletResponse response) {
        try {
            return response.getWriter();
        }
        catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
