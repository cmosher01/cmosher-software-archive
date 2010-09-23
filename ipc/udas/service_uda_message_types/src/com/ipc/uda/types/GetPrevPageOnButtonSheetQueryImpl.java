/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.manager.ButtonManager;
import com.ipc.uda.service.callproc.ButtonSheet;
import com.ipc.uda.service.callproc.UdaButton;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.EntityHelper;

//REVIEW: 10503 This impl is no longer needed and should be removed

/**
 * @author Veena Makam
 * 
 */
public class GetPrevPageOnButtonSheetQueryImpl extends GetPrevPageOnButtonSheetQuery implements
        ExecutableWithContext
{
    private static final int FIRST_PAGE_NUM = 1;

    @XmlTransient
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        Map<Integer, UdaButton> pageMap = null;
        final SecurityContext basicSecContext = this.ctx.getSecurityContext();
        ButtonSheetQueryResultType resultType = null;

        final ButtonManager dsButtonMgr = new ButtonManager(basicSecContext);
        if (dsButtonMgr == null)
        {
            Log.logger().debug(
                    "Manager object is Null from DS for user " + this.ctx.getUser().getName());
            return new Nothing<ExecutionResult>();
        }

        // wrap the pages
        if (this.getCurrentPageNumber() == GetPrevPageOnButtonSheetQueryImpl.FIRST_PAGE_NUM)
        {
            pageMap = this.ctx.getCallContext().getButtonSheet().getButtonsOfPage(
                    ButtonSheet.MAX_NUM_OF_PAGES_ON_BUTTON_SHEET);
        }
        else
        // get the previous page of the button sheet
        {
            pageMap = this.ctx.getCallContext().getButtonSheet().getButtonsOfPage(
                    this.currentPageNumber - 1);
        }

        if (pageMap == null || pageMap.isEmpty())
        {
            Log.logger().debug(
                    "Button sheet for the user " + this.ctx.getUser().getName() + " is empty/NULL");
            return new Nothing<ExecutionResult>();
        }
        else
        {
            resultType = new ButtonSheetQueryResultType();
            for (final UdaButton udaButton : pageMap.values())
            {
                try
                {
                    if (udaButton != null)
                    {
                        final Button dsButton = dsButtonMgr.getById(Integer.parseInt(udaButton
                                .getId().toString()));
                        ButtonType udaButtonType = new ButtonType();
                        udaButtonType = EntityHelper.fromDSButton(dsButton);

                        resultType.getButton().add(udaButtonType);

                        DataServicesSubscriptionHelper.createSubscriptionsTo(Button.class
                                .getSimpleName(), dsButton.getId(), this.ctx);
                    }
                }
                catch (final Throwable lException)
                {
                    throw new ExecutionException(lException);
                }
            }
        }

        final QueryResult qResult = new QueryResult();
        qResult.setButtonSheet(resultType);

        return new Optional<ExecutionResult>(qResult);
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

}
