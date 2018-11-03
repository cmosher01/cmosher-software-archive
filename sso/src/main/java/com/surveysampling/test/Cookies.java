package com.surveysampling.test;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


/**
 * Servlet that dumps cookies.
 *
 * Created by cmosher on 12/3/14.
 */
@WebServlet(urlPatterns = {"/cookies"})
public class Cookies extends HttpServlet {

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;
        try {
            out = buildResponseWriter(response);
            printResponse(request, out);
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static void printResponse(final HttpServletRequest request, final PrintWriter out) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("<meta charset=\"utf-8\">");
        out.println("<title>Cookies</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<table>");
        out.println("<thead>");
        printCookieHead(out);
        out.println("</thead>");
        out.println("<tbody>");
        printCookies(request, out);
        out.println("</tbody>");
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    private static void printCookies(final HttpServletRequest request, final PrintWriter out) {
        final Cookie[] cookies = getCookiesSafely(request);
        for (final Cookie cookie : cookies) {
            printCookie(cookie, out);
        }
    }

    private static Cookie[] getCookiesSafely(HttpServletRequest request) {
        Cookie[] cookies = null;
        try {
            cookies = request.getCookies();
        } catch (final Throwable ignored) {
            /* ignore any exceptions */
        }
        if (cookies == null) {
            /* if no cookies exist, return an empty array instead of null */
            cookies = new Cookie[] {};
        }
        return cookies;
    }

    private static void printCookieHead(final PrintWriter out) {
        out.println("<tr>");
        out.println("<th>");
        out.println("Name");
        out.println("</th>");
        out.println("<th>");
        out.println("Value");
        out.println("</th>");
        out.println("</tr>");
    }

    private static void printCookie(final Cookie cookie, final PrintWriter out) {
        out.println("<tr>");
        out.println("<td>");
        out.println(cookie.getName());
        out.println("</td>");
        out.println("<td>");
        out.println(cookie.getValue());
        out.println("</td>");
        out.println("</tr>");
    }

    private static PrintWriter buildResponseWriter(final HttpServletResponse response) {
        try {
            return response.getWriter();
        }
        catch (final Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
