package com.ipc.uda.types;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;

public class GetPersonalContactHistoryQueryImpl extends GetPersonalContactHistoryQuery implements
        ExecutableWithContext
{

    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

}
