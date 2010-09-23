/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.diag;

import javax.ws.rs.core.Response;

/**
 * @author Bhavya
 */
public class DiagExecutionException extends Exception
{
	private int error_code = 200;
	private String reason ="NA";
    /**
     * 
     */
    public DiagExecutionException()
    {
    	super();
    }

	
	/**
     * @param message
     */
    public DiagExecutionException(int error_code , String message)
    {
    	super(message);
    	error_code = error_code;
    	reason = message;
    	
        
    }
 
    public static Response getResponse(int error)
    {
    	return Response.status(error).build();
    }
    public Response getResponse()
    {
    	return Response.status(error_code).build();
    }
    
}
