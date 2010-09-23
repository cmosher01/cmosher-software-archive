/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service;

/**
 * @author mordarsd
 * 
 */
public class QueryExecutionHandler extends AbstractExecutionHandlerDelegate
{

    private static final String JNDI_NAME = "uda.ejb.local.session.QueryExecutionHandler";

    @Override
    protected String getJndiName()
    {
        return QueryExecutionHandler.JNDI_NAME;
    }
}
