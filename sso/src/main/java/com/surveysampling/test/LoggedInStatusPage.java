package com.surveysampling.test;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;


/**
 * Created by cmosher on 10/20/14.
 */
@WebServlet(urlPatterns = {"/loggedInStatus"})
public class LoggedInStatusPage extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;
        try {
            out = buildResponseWriter(response);
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"en\">");
            out.println("<head>");
            out.println("<meta charset=\"utf-8\">");
            out.println("<title>Logged In Status</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>" + getHostName() + ": " + (isLoggedIn(request) ? "Logged in as user " + getLoggedInUserName(request) : "Not logged in") + " / sessionID==" + getSessionID(request) + " data: " + getSessionData(request) + "</p>");
            out.println("</body>");
            out.println("</html>");
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static String getSessionID(HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return "[session does not exist]";
        }
        return session.getId();
    }

    private static String getSessionData(HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return "[session does not exist]";
        }
        final Object obj = session.getAttribute("myObject");
        if (obj == null) {
            return "[object does not exist in session]";
        }
        return obj.toString();
    }

    private static String getLoggedInUserName(HttpServletRequest request) {
        return request.getUserPrincipal().getName();
    }

    private static boolean isLoggedIn(HttpServletRequest request) {
        return request.getUserPrincipal() != null;
    }

    private static PrintWriter buildResponseWriter(HttpServletResponse response) {
        try {
            return response.getWriter();
        }
        catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private static String getHostName() {
        return System.getProperty("jboss.node.name");
    }
}
