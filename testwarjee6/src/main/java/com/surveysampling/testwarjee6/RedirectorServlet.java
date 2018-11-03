package com.surveysampling.testwarjee6;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectorServlet extends HttpServlet
{
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("text/html");
        resp.sendRedirect("http://www.google.com/?invalid=<#bad>");
//        resp.sendRedirect("http://www.google.com/?invalid=bad");
    }
}
