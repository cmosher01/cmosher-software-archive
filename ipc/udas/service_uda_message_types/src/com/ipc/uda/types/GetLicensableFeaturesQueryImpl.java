package com.ipc.uda.types;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;

public class GetLicensableFeaturesQueryImpl extends GetLicensableFeaturesQuery implements ExecutableWithContext
{
	private UserContext ctx;
	 
    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {      
        final LicensableFeaturesResultType result = new LicensableFeaturesResultType();
        
        result.licensableFeatures = new LicensableFeaturesType();
        result.licensableFeatures.setEnterpriseCommunication(true);
        result.licensableFeatures.setTraderLite(true);
        result.licensableFeatures.setTurretCompanion(true);
        result.licensableFeatures.setClickToDial(true);
        result.licensableFeatures.setPrecense(false);
        result.licensableFeatures.setBcp(false);
        result.licensableFeatures.setQuickFinder(true);
        result.licensableFeatures.setScl(2);
        
        final QueryResult qResult = new QueryResult();
        qResult.setLicensableFeaturesResult(result);

        return new Optional<ExecutionResult>(qResult);
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }
}
