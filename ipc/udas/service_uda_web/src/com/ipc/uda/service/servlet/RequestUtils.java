package com.ipc.uda.service.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;

final class RequestUtils
{
    /**
     * The HTTP GET query parameter from which this servlet reads
     * the XML for a query or command.
     */
    public static final String UDA_XML_QUERY_PARAMETER = "message";
    
    public static final String UDA_DEVICE_ID_HEADER = "X-UDA-Device-ID";
    
    private static final String USE_MOCK_OBJECTS_PARAMETER = "useMockObjects";



    /**
     * Retrieves a properly configured {@link BufferedReader} that can be used to read
     * from the HTTP request's XML parameter ("message").
     * @param request the HTTP request channel
     * @return the reader to read from the XML parameter ("message")
     */
    public static BufferedReader getXmlFromRequestParameter(final HttpServletRequest request)
    {
        try
        {
            request.setCharacterEncoding("UTF-8");
            return new BufferedReader(new StringReader(getXmlParameterOrEmpty(request)));
        }
        catch (final Throwable shouldNeverHappen)
        {
            throw new IllegalStateException(shouldNeverHappen);
        }
    }

    /**
     * Retrieves a properly configured {@link BufferedReader} that can be used to read
     * from the HTTP request's body.
     * @param request the HTTP request channel
     * @return the reader to read from the request body
     */
    public static BufferedReader getXmlFromRequestBody(final HttpServletRequest request)
    {
        try
        {
            return new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF-8"));
        }
        catch (final Throwable shouldNeverHappen)
        {
            throw new IllegalStateException(shouldNeverHappen);
        }
    }

    public static String getAuthorizationHeader(final HttpServletRequest request)
    {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null)
        {
            authHeader = "";
        }
        return authHeader;
    }
    
    /**
     * Extracts the X-UDA-Device-ID HTTP Header from the HTTP Request
     * 
     * @param request
     * @return  The DeviceID
     */
    public static String getDeviceIDHeader(final HttpServletRequest request)
    {
        String deviceID = request.getHeader(UDA_DEVICE_ID_HEADER);
        if ((deviceID == null) || (deviceID.isEmpty()))
        {
            throw new IllegalArgumentException(
                    "Unable to process request; HTTP Header [" + 
                    UDA_DEVICE_ID_HEADER + "] is missing!");
        }
        return deviceID;
    }

    /**
     * Checks for useMockObjects parameter
     * 
     * @param request
     * @return  true if using mock objects
     */
    public static boolean isUsingMockObjects(final HttpServletRequest request)
    {
        final String usingMockObjects = request.getParameter(RequestUtils.USE_MOCK_OBJECTS_PARAMETER);
        return Boolean.parseBoolean(usingMockObjects);
    }
    
    
    /**
     * Retrieves the value of the XML parameter ("message"). If the parameter does not exist, then this
     * method returns an empty string.
     * @param request
     * @return value of the XML parameter, or empty; never null
     */
    private static String getXmlParameterOrEmpty(final HttpServletRequest request)
    {
        final String xml = request.getParameter(RequestUtils.UDA_XML_QUERY_PARAMETER);
        if (xml == null)
        {
            return "";
        }
        return xml;
    }
    
    
}
