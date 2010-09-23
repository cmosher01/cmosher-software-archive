/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.CommunicationHistory;
import com.ipc.ds.entity.manager.ButtonManager;
import com.ipc.ds.entity.manager.CommunicationHistoryManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * @author Veena Makam
 * 
 */
public class GetContactHistoryFromButtonPageQueryImpl extends GetContactHistoryFromButtonPageQuery
        implements ExecutableWithContext
{

    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute()
    {
        final SecurityContext basSecCtx = this.ctx.getSecurityContext();
        // get all the relevant manager objects
        final ButtonManager buttonMgr = new ButtonManager(basSecCtx);
        if (buttonMgr == null)
        {
            Log.logger()
                    .debug(
                            "ButtonManager object is Null from DS for user "
                                    + this.ctx.getUser().getName());
            return returnNothing();
        }

        final CommunicationHistoryManager commHistMgr = new CommunicationHistoryManager(basSecCtx);
        if (commHistMgr == null)
        {
            Log.logger().debug(
                    "CommunicationHistoryManager object is Null from DS for user "
                            + this.ctx.getUser().getName());
            return returnNothing();
        }

        List<CommunicationHistory> commHistList = null;
        InstanceContactHistoryResult qryResult = null;
        try
        {
            // get the button object for which history is to be determined
            final Button buttonDS = buttonMgr.getById(Integer.parseInt(this.buttonId.toString()));
            commHistList = commHistMgr.findByButtonNumberEqualTo(buttonDS.getButtonNumber());
            if (commHistList == null || commHistList.isEmpty())
            {
                Log.logger().debug(
                        "Communication history for the Button ID: " + this.buttonId + " is null");
                return returnNothing();
            }

            // populate query result
            qryResult = new InstanceContactHistoryResult();
            for (final CommunicationHistory commHistDS : commHistList)
            {
                HistoryType udaCommHist = new HistoryType();
                udaCommHist = UDAAndDSEntityUtil.mapDSToUDACommHist(commHistDS, udaCommHist,
                        this.ctx);
                qryResult.getContactHistory().add(udaCommHist);
            }

            final QueryResult qResult = new QueryResult();
            qResult.setInstanceContactHistory(qryResult);
            return new Optional<ExecutionResult>(qResult);
        }
        catch (final Throwable e)
        {
        }
        return returnNothing();
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

    private Optional<ExecutionResult> returnNothing()
    {
        return new Nothing<ExecutionResult>();
    }
}
