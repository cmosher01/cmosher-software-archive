package com.surveysampling.test;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


/**
 * Created by cmosher on 10/20/14.
 */
@WebServlet(urlPatterns = {"/logOut"})
public class LogOutPage extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        request.logout();

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;
        try {
            out = buildResponseWriter(response);
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"en\">");
            out.println("<head>");
            out.println("<meta charset=\"utf-8\">");
            out.println("<title>Log Out</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>OK</p>");
            out.println("</body>");
            out.println("</html>");
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
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
