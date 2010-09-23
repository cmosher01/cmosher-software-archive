/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service;



/**
 * @author mordarsd
 * 
 */
public class CommandExecutionHandler extends AbstractExecutionHandlerDelegate
{

    private static final String JNDI_NAME = "uda.ejb.local.session.CommandExecutionHandler";

    @Override
    protected String getJndiName()
    {
        return CommandExecutionHandler.JNDI_NAME;
    }

}
