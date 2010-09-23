package com.ipc.uda.service.callproc;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.UserSpeakerChannelManager;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.service.util.smc.Actions;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * @author sharmar
 * @author mosherc
 */

public class SpeakerSheet 
{
    public static final int MAX_NUM_OF_SPEAKER_CHANNEL = 16;

    private final UserContext ctx;
    private final List<Optional<UdaSpeaker>> udaSpeakerList = new ArrayList<Optional<UdaSpeaker>>();
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);

    private final ConcurrentMap<ButtonAppearance, Set<SpeakerCall>> mapAppearanceToCall = new ConcurrentHashMap<ButtonAppearance, Set<SpeakerCall>>();

    public SpeakerSheet(final UserContext ctx)
    {
        this.ctx = ctx;
        this.udaSpeakerList.addAll(Collections.<Optional<UdaSpeaker>>nCopies(SpeakerSheet.MAX_NUM_OF_SPEAKER_CHANNEL, new Nothing<UdaSpeaker>()));
    }

    public void initialize()
    {
        if (!this.initialized.compareAndSet(false,true))
        {
            return;
        }

        try
        {
            final UserCDI userCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);

            final UserSpeakerChannelManager usrSprChlMgr = new UserSpeakerChannelManager(this.ctx.getSecurityContext());
            final List<UserSpeakerChannel> speakers = usrSprChlMgr.getUserSpeakerChannelsFor(userCDI);
            for (final UserSpeakerChannel speaker : speakers)
            {
                final Optional<UdaSpeaker> udaSpeaker = SpeakerFactory.create(speaker, this.ctx);
                if (udaSpeaker.exists())
                {
                    addSpeaker(udaSpeaker.get());
                }
            }
        }
        catch (final Throwable e)
        {
            Actions.handleActionException(e);
        }
    }
    
    public void getAllSpeakers(final Collection<UdaSpeaker> addTo)
    {
        for (final Optional<UdaSpeaker> optSpeaker : this.udaSpeakerList)
        {
            if (optSpeaker.exists())
            {
                addTo.add(optSpeaker.get());
            }
        }
    }    

    public Optional<UdaSpeaker> getSpeaker(final int speaker)
    {
        try
        {
            return this.udaSpeakerList.get(speaker-1);
        }
        catch (final Throwable e)
        {
            Log.logger().info("Invalid speaker number: "+speaker,e);
            return new Nothing<UdaSpeaker>();
        }
    }


    public void startFSMs()
    {
        if (!this.started.compareAndSet(false,true))
        {
            return;
        }
        for (final Optional<UdaSpeaker> optSpeaker : this.udaSpeakerList)
        {
            if (optSpeaker.exists())
            {
                optSpeaker.get().getCall().startFSM();
            }
        }
    }
    
    public void addSpeaker(final UdaSpeaker udaSpeaker)
    {
        if (udaSpeaker == null)
        {
            throw new IllegalArgumentException("speaker cannot be null");
        }
        
        this.udaSpeakerList.set(udaSpeaker.getDsSpeaker().getSpeakerNumber()-1, new Optional<UdaSpeaker>(udaSpeaker));

        
        final ButtonAppearance aor = udaSpeaker.getAppearance();
        this.mapAppearanceToCall.putIfAbsent(aor, Collections.<SpeakerCall> synchronizedSet(new HashSet<SpeakerCall>()));
        final Set<SpeakerCall> set = this.mapAppearanceToCall.get(aor);
        if (set == null)
        {
            // could only happen if some other thread just removed the speaker again,
            // which will probably never ever happen, but we check in order to satisfy Klockwork
            return;
        }
        set.add(udaSpeaker.getCall());
        if (this.started.get())
        {
            udaSpeaker.getCall().startFSM();
        }
    }
    
    public Set<SpeakerCall> getCalls(final ButtonAppearance appearance)
    {
        final Set<SpeakerCall> setOrNull = this.mapAppearanceToCall.get(appearance);
        if (setOrNull == null)
        {
            return Collections.<SpeakerCall> unmodifiableSet(new HashSet<SpeakerCall>());
        }
        return Collections.<SpeakerCall> unmodifiableSet(setOrNull);
    }

    public void pressGroupPtt(int group)
    {
        for (final Optional<UdaSpeaker> optSpeaker : this.udaSpeakerList)
        {
            if (optSpeaker.exists())
            {
                optSpeaker.get().getCall().udacPressGroupPTT(group);
            }
        }
    }

    public void releaseGroupPtt(int group)
    {
        for (final Optional<UdaSpeaker> optSpeaker : this.udaSpeakerList)
        {
            if (optSpeaker.exists())
            {
                optSpeaker.get().getCall().udacReleaseGroupPTT(group);
            }
        }
    }
}
