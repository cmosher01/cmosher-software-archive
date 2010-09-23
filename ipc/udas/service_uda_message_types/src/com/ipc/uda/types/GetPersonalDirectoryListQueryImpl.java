/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * This class is responsible for fetching the contact list of personal directory for a particular
 * user
 * 
 * @author Veena Makam
 * 
 */
public class GetPersonalDirectoryListQueryImpl extends GetPersonalDirectoryListQuery implements
        ExecutableWithContext
{
    private UserContext ctx;

    public GetPersonalDirectoryListQueryImpl()
    {

    }

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {

        final PersonalDirectoryListResult dlResultObj = new PersonalDirectoryListResult();

        try
        {
            dlResultObj.contact = UDAAndDSEntityUtil.getPersonalContacts(this.ctx.getUser()
                    .getName(), this.ctx);

        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }
        final QueryResult qResult = new QueryResult();
        qResult.setPersonalDirectoryList(dlResultObj);

        return new Optional<ExecutionResult>(qResult);

    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

}
