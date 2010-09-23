/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.internal.manager.base.UserSpeakerChannelBaseManager;
import com.ipc.ds.entity.manager.ResourceAORManager;
import com.ipc.ds.entity.manager.UserCDIManager;
import com.ipc.ds.entity.manager.UserSpeakerChannelManager;
import com.ipc.uda.event.ExecutableResultQueue;
import com.ipc.uda.service.callproc.ButtonFactory;
import com.ipc.uda.service.callproc.SpeakerSheet;
import com.ipc.uda.service.callproc.UdaSpeaker;
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
 * This class is responsible to Add a UserSpeakerChannel to the database for the given user
 * 
 * @author Veena Makam
 * @author sharmar
 * 
 */
public class AddSpeakerChannelCommandImpl extends AddSpeakerChannelCommand implements
        ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        try
        {
            // update the Speaker Sheet
            final SpeakerSheet speakerSheet = this.ctx.getCallContext().getSpeakerSheet();
            final UserSpeakerChannel usrSprChl = UserSpeakerChannelBaseManager
                    .NewUserSpeakerChannel();

            final ResourceAORManager mgrAor = new ResourceAORManager(this.ctx.getSecurityContext());
            final ResourceAOR aor = mgrAor.getResourceAORFor(usrSprChl);
            final ButtonAppearance aorAndAppearance = ButtonFactory.createAppearance(aor, usrSprChl
                    .getAppearance());
            final UdaSpeaker udaSpeaker = new UdaSpeaker(usrSprChl, aorAndAppearance);

            speakerSheet.addSpeaker(udaSpeaker);

            populateSpeakerChannel(usrSprChl);

            final UserCDI usrCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);
            usrCDI.addToUserSpeakerChannels(usrSprChl);

            final UserCDIManager usrCDIMgr = new UserCDIManager(this.ctx.getSecurityContext());
            usrCDIMgr.save(usrCDI);

            final SecurityContext basicSecContext = this.ctx.getSecurityContext();
            final UserSpeakerChannelManager usrSprChlMgr = new UserSpeakerChannelManager(
                    basicSecContext);
            usrSprChlMgr.save(usrSprChl);

            Log.logger().debug(
                    "Added UserSpeakerChannel successfully with the ID: " + usrSprChl.getId()
                            + " for user: " + this.ctx.getUserName());

            // send the speaker channel updated event after successful addition of the speaker
            // channel into db
            final SpeakerChannelUpdatedEvent spUpdEve = SpeakerUtil.populateSpeakerChlUpdEvent(ctx,
                    usrSprChl);
            final Event eve = new Event();
            eve.setSpeakerChannelUpdated(spUpdEve);
            ExecutableResultQueue.<Event> send(eve, ctx.getUser(), ctx.getUserID().getDeviceID());

            // subscribe for events from DS
            DataServicesSubscriptionHelper.createSubscriptionsTo(UserSpeakerChannel.class
                    .getSimpleName(), usrSprChl.getId(), this.ctx);
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
     * Populates UserSpeakerChannel with all the input values
     * 
     * @param usrSprChl UserSpeakerChannel
     * @throws Throwable
     */
    private void populateSpeakerChannel(final UserSpeakerChannel usrSprChl) throws Throwable
    {
        try
        {
            usrSprChl.setAppearance(this.getAppearance());
            usrSprChl.setLabel(this.getName());
            usrSprChl.setSpeakerNumber(this.getSpeakerNumber());

            // check for null - optional attributes
            if (this.getResourceAor() != null && !this.getResourceAor().isEmpty())
            {
                final ResourceAORManager resAORMgr = new ResourceAORManager(this.ctx
                        .getSecurityContext());
                final ResourceAOR resourceAOR = resAORMgr.findByResourceAOREqualing(this
                        .getResourceAor());

                // validate resourceAOR
                if (resourceAOR == null)
                {
                    Log.logger().debug(
                            "ResourceAOR is null for resourceAOR: " + this.getResourceAor()
                                    + " for user: " + this.ctx.getUserName());
                    throw new Exception("ResourceAOR is null for resourceAOR: "
                            + this.getResourceAor() + " for user: " + this.ctx.getUserName());
                }
                usrSprChl.setResourceAOR(resourceAOR);
            }

            usrSprChl.setIsInGroup1(this.isInGroup1());
            usrSprChl.setIsInGroup2(this.isInGroup2());
        }
        catch (final Throwable lException)
        {
            throw lException;
        }
    }
}
