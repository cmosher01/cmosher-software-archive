package com.ipc.uda.service.servlet;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletResponse;

final class ResponseUtils
{
    /**
     * Retrieves and configures a writer that can be used to write to the given HTTP
     * response channel. Also configures the response text/xml using UTF-8.
     * @param response the HTTP response channel
     * @return the writer to write to the given HTTP response channel
     */
    public static BufferedWriter getResponseWriter(final HttpServletResponse response)
    {
        try
        {
            response.setContentType("text/xml");
            response.setCharacterEncoding("UTF-8");
            return new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),"UTF-8"));
        }
        catch (final Throwable shouldNeverHappen)
        {
            throw new IllegalStateException(shouldNeverHappen);
        }
    }
}
