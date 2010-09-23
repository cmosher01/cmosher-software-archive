package com.ipc.uda.types.old;

import javax.xml.bind.annotation.XmlTransient;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.LicenseListResult;
import com.ipc.uda.types.QueryResult;
import com.ipc.uda.types.LicenseListResult.Feature;
/*
public class GetLicenseListQueryImpl extends GetLicenseListQuery implements ExecutableWithContext
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
        final LicenseListResult.Feature featureTesting = new LicenseListResult.Feature();
        featureTesting.setLicensed(true);
        featureTesting.setName("TESTING");

        final LicenseListResult.Feature featureTrial = new LicenseListResult.Feature();
        featureTrial.setLicensed(false);
        featureTrial.setName("TRIAL");

        final LicenseListResult res = new LicenseListResult();
        res.getFeature().add(featureTesting);
        res.getFeature().add(featureTrial);

        final QueryResult qr = new QueryResult();
        qr.setLicenseList(res);

        return new Optional<ExecutionResult>(qr);
    }

}
*/