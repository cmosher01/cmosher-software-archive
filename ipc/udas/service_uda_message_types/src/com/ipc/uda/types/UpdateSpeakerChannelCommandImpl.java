/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.ResourceAORManager;
import com.ipc.ds.entity.manager.UserSpeakerChannelManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.SpeakerUtil;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * This class is responsible to update the user speaker channel entity in DS
 * 
 * @author Veena Makam
 * 
 */
public class UpdateSpeakerChannelCommandImpl extends UpdateSpeakerChannelCommand implements
        ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {

        try
        {
            updateSpeakerChannel();
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }
        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(final UserContext unused)
    {
        this.ctx = unused;
    }

    /**
     * @throws ExecutionException
     * 
     */
    private void updateSpeakerChannel() throws ExecutionException
    {
        // get the basic security context object
        final SecurityContext basicSecContext = this.ctx.getSecurityContext();

        final UserSpeakerChannelManager usrSprChlMgr = new UserSpeakerChannelManager(
                basicSecContext);
        try
        {
            UserSpeakerChannel usrSprChl = null;
            final UserCDI usrCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);
            final List<UserSpeakerChannel> usrSprChlLst = usrSprChlMgr
                    .getUserSpeakerChannelsFor(usrCDI);

            if (usrSprChlLst != null && !usrSprChlLst.isEmpty())
            {
                usrSprChl = SpeakerUtil.getUserSpeakerChannelWithInput(usrSprChlLst, this
                        .getSpeakerNumber());

                if (usrSprChl != null)
                {
                    usrSprChl.setSpeakerNumber(this.getSpeakerNumber());
                    usrSprChl.setLabel(this.getName());
                    usrSprChl.setIsInGroup1(this.isInGroup1());
                    usrSprChl.setIsInGroup2(this.isInGroup2());

                    // check for null - optional attributes
                    if (this.getResourceAor() != null && !this.getResourceAor().isEmpty())
                    {

                        // find the ResourceAOR with the given input
                        final ResourceAORManager resAORMgr = new ResourceAORManager(this.ctx
                                .getSecurityContext());
                        final ResourceAOR resAORObj = resAORMgr.findByResourceAOREqualing(this
                                .getResourceAor());

                        // validate resource aor from DS
                        if (resAORObj == null || resAORObj.getResourceAOR() == null)
                        {
                            Log.logger().debug(
                                    "ResourceAOR: " + this.getResourceAor()
                                            + " provided by UDAC is invalid for the user: "
                                            + this.ctx.getUserName() + " for the channel number: "
                                            + this.getSpeakerNumber());
                            throw new ExecutionException("ResourceAOR: " + this.getResourceAor()
                                    + " provided by UDAC is invalid for the user: "
                                    + this.ctx.getUserName() + " for the channel number: "
                                    + this.getSpeakerNumber());
                        }

                        usrSprChl.setResourceAOR(resAORObj);
                    }
                    usrSprChl.setAppearance(this.getAppearance());
                    DataServicesSubscriptionHelper.createSubscriptionsTo(UserSpeakerChannel.class
                            .getSimpleName(), usrSprChl.getId(), this.ctx);
                    usrSprChlMgr.save(usrSprChl);
                }
                else
                {
                    throw new ExecutionException("UserSpeakerChannel for the channel number "
                            + this.getSpeakerNumber() + " does not exist in DB for user "
                            + this.ctx.getUserName());
                }
            }
            else
            {
                throw new ExecutionException("UserSpeakerChannel for the channel number "
                        + this.getSpeakerNumber() + " does not exist in DB for user "
                        + this.ctx.getUserName());
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }
    }
}
