/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.ArrayList;
import java.util.Collection;

import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.ButtonResourceAppearance;
import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.uda.service.callproc.ButtonSheet;
import com.ipc.uda.service.callproc.UdaButton;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.util.ButtonUtil;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.EntityHelper;

/**
 * @author Jagannadham Dulipala Added the basic logic As per Create ButtonSheet design
 * 
 */

public class GetButtonSheetQueryImpl extends GetButtonSheetQueryType implements
        ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        // get all buttons from the button sheet application
        final Collection<UdaButton> udaButtons = new ArrayList<UdaButton>(
                ButtonSheet.MAX_NUM_OF_BUTTONS);
        
        this.ctx.getCallContext().initializeButtonSheet();
        this.ctx.getCallContext().getButtonSheet().getAllButtons(udaButtons);

        final ButtonSheetQueryResultType resultType = new ButtonSheetQueryResultType();
        for (final UdaButton udaButton : udaButtons)
        {
            if (udaButton != null)
            {
                final Button button = udaButton.getDsButton();
                final ButtonType udaButtonType = EntityHelper.fromDSButton(button);

                try
                {
                    final ButtonResourceAppearance appearance = ButtonUtil
                            .getButtonResourceAppearance(button, this.ctx);
                    final ResourceAOR resourceAor = ButtonUtil.getResourceAor(appearance, this.ctx);

                    udaButtonType.setAppearance(appearance.getAppearance());
                    udaButtonType.setResourceAor(resourceAor.getResourceAOR());
                }
                catch (final Throwable e)
                {
                    throw new ExecutionException(
                            "Unexpected exception caught while attempting to perform GetButtonSheetQuery: reason: "
                                    + e.getMessage(), e);
                }

                resultType.getButton().add(udaButtonType);

                DataServicesSubscriptionHelper.createSubscriptionsTo(Button.class.getSimpleName(),
                        udaButton.getDsButton().getId(), this.ctx);
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
