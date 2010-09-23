package com.ipc.uda.types;



import javax.xml.bind.annotation.XmlTransient;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;



public class SetPageSizeCommandImpl extends SetPageSizeCommand implements ExecutableWithContext
{
    
    private UserContext ctx;

    @Override
    public void setUserContext(UserContext ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public Optional<ExecutionResult> execute()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
