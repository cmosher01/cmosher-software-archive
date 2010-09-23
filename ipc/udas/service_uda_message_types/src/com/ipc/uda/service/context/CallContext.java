/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.context;

import com.ipc.uda.service.callproc.ButtonSheet;
import com.ipc.uda.service.callproc.ConferenceCall;
import com.ipc.uda.service.callproc.IcmApp;
import com.ipc.uda.service.callproc.SpeakerSheet;

/**
 * @author mordarsd
 * @author sharmar
 * 
 */
public final class CallContext
{
    private final ButtonSheet buttonSheet;
    private final IcmApp icmApp = new IcmApp();
    private final SpeakerSheet speakerSheet;
    

    private final ConferenceCall left = new ConferenceCall();


    /**
     * 
     * @param userCtx
     */
    public CallContext(final UserContext userCtx)
    {
        this.buttonSheet = new ButtonSheet(userCtx);
        this.speakerSheet = new SpeakerSheet(userCtx);
    }
    
    public void initializeButtonSheet()
    {
        this.buttonSheet.initialize();
    }

    public ButtonSheet getButtonSheet()
    {
        return this.buttonSheet;
    }
    
    public void initializeSpeakerSheet()
    {
        this.speakerSheet.initialize();
    }
    public SpeakerSheet getSpeakerSheet()
    {
        return this.speakerSheet;
    }
    
    public IcmApp getIcmApp()
    {
        return this.icmApp;
    }
    
    public void dispose()
    {
        // TODO what do we need this for?
    }

    /**
     * Gets the conference call on the left handset (which could be empty)
     * @return optional left call
     */
    public ConferenceCall getLeft()
    {
        return this.left;
    }

    public void startFSMs()
    {
        this.buttonSheet.startFSMs();
        this.speakerSheet.startFSMs();
    }
}
