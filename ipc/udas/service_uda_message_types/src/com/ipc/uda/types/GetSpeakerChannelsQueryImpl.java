/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.ArrayList;
import java.util.Collection;

import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.uda.service.callproc.SpeakerSheet;
import com.ipc.uda.service.callproc.UdaSpeaker;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;

/**
 * @author parkerj
 * @author sharmar
 */

public class GetSpeakerChannelsQueryImpl extends GetSpeakerChannelsQuery implements
        ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        this.ctx.getCallContext().initializeSpeakerSheet();

        // get all speakers from the speaker sheet application
        final Collection<UdaSpeaker> udaSpeaksrs = new ArrayList<UdaSpeaker>(
                SpeakerSheet.MAX_NUM_OF_SPEAKER_CHANNEL);
        this.ctx.getCallContext().getSpeakerSheet().getAllSpeakers(udaSpeaksrs);

        final SpeakerChannelResultType spkChannelResultType = new SpeakerChannelResultType();

        for (final UdaSpeaker udaSpeaker : udaSpeaksrs)
        {
            // get the speaker channels list for the given user from DB
            SpeakerChannelType speakerChannels = getSpeakerChannels(udaSpeaker);
            spkChannelResultType.getSpeakerChannel().add(speakerChannels);
        }

        // update the result back to the client
        final QueryResult qResult = new QueryResult();
        qResult.setSpeakerChannelResult(spkChannelResultType);

        return new Optional<ExecutionResult>(qResult);
    }

    private SpeakerChannelType getSpeakerChannels(UdaSpeaker udaSpeaker)
    {
        final UserSpeakerChannel dsUsrSpeakerChannel = udaSpeaker.getDsSpeaker();
        DataServicesSubscriptionHelper.createSubscriptionsTo(UserSpeakerChannel.class
                .getSimpleName(), dsUsrSpeakerChannel.getId(), ctx);

        final SpeakerChannelType udaSprChnType = new SpeakerChannelType();

        udaSprChnType
                .setStatus((dsUsrSpeakerChannel.getActiveStatus() ? EnumSpeakerChannelStatusType.ON
                        : EnumSpeakerChannelStatusType.OFF));
        udaSprChnType.setName(dsUsrSpeakerChannel.getLabel());

        udaSprChnType.setResourceAor(udaSpeaker.getAppearance().getAor());

        udaSprChnType.setInGroup1(dsUsrSpeakerChannel.getIsInGroup1());
        udaSprChnType.setInGroup2(dsUsrSpeakerChannel.getIsInGroup2());

        return udaSprChnType;
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }
}
