/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types.util;

import java.util.List;

import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.ResourceAORManager;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.EnumSpeakerChannelStatusType;
import com.ipc.uda.types.SpeakerChannelType;
import com.ipc.uda.types.SpeakerChannelUpdatedEvent;

/**
 * @author Veena Makam
 * 
 */
public class SpeakerUtil
{

    /**
     * Retrieves the UserSpeakerChannel for the given speaker number from the input list
     * 
     * @param usrSprChlLst List<UserSpeakerChannel>
     * @param spkrNo int
     * @return UserSpeakerChannel usrSprChl
     */
    public static UserSpeakerChannel getUserSpeakerChannelWithInput(
            final List<UserSpeakerChannel> usrSprChlLst, final int spkrNo)
    {
        UserSpeakerChannel usrSprChl = null;
        for (final UserSpeakerChannel userSpeakerChannel : usrSprChlLst)
        {
            if (userSpeakerChannel.getSpeakerNumber() == spkrNo)
            {
                usrSprChl = userSpeakerChannel;
            }
        }

        return usrSprChl;
    }

    /**
     * Populates SpeakerChannelUpdatedEvent for the given UserSpeakerChannel entity
     * 
     * @param usrCtx UserContext
     * @param usrSprChl UserSpeakerChannel
     * @return
     */
    public static SpeakerChannelUpdatedEvent populateSpeakerChlUpdEvent(final UserContext usrCtx,
            final UserSpeakerChannel usrSprChl)
    {
        final SpeakerChannelUpdatedEvent spUpdEve = new SpeakerChannelUpdatedEvent();
        final SpeakerChannelType speakerChannel = new SpeakerChannelType();
        speakerChannel.setSpeakerNumber(usrSprChl.getSpeakerNumber());
        speakerChannel.setStatus((usrSprChl.getActiveStatus() ? EnumSpeakerChannelStatusType.ON
                : EnumSpeakerChannelStatusType.OFF));
        speakerChannel.setVolumeLevel(usrSprChl.getVolume());
        speakerChannel.setAppearance(usrSprChl.getAppearance());
        speakerChannel.setName(usrSprChl.getLabel());
        speakerChannel.setInGroup1(usrSprChl.getIsInGroup1());
        speakerChannel.setInGroup2(usrSprChl.getIsInGroup2());

        // set the resourceAOR for the given UserSpeakerChannel
        final ResourceAORManager resAORMgr = new ResourceAORManager(usrCtx.getSecurityContext());

        try
        {
            final ResourceAOR dsResAOR = resAORMgr.getResourceAORFor(usrSprChl);
            if (dsResAOR != null && dsResAOR.getResourceAOR() != null)
            {
                speakerChannel.setResourceAor(dsResAOR.getResourceAOR());
            }
            else
            {
                speakerChannel.setResourceAor("");
            }
        }
        catch (final Throwable lException)
        {
            Log.logger().debug(
                    "Unable to get the ResourceAOR for the speaker with channel number: "
                            + usrSprChl.getSpeakerNumber());
        }

        spUpdEve.setSpeakerChannel(speakerChannel);

        return spUpdEve;
    }

}
