/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.ejb.session;



import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;



/**
 * @author mordarsd
 * 
 */
public interface ExecutionHandlerLocalHome extends EJBLocalHome
{

    ExecutionHandlerLocal create() throws CreateException;

}
