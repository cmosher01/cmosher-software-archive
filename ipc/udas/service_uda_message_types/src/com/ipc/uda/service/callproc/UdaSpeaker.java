package com.ipc.uda.service.callproc;



import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.UID;


/**
 * @author sharmar
 *
 */
public class UdaSpeaker
{
    
    private final UID id;
    private final UserSpeakerChannel dsSpkChl;
    private final ButtonAppearance appearance;
    private SpeakerCall speakerCall;

    public UdaSpeaker(final UserSpeakerChannel dsSpkChl, final ButtonAppearance appearance)
    {
        this.dsSpkChl = dsSpkChl;
        this.appearance = appearance;
        this.id = UID.fromDataServicesID(this.dsSpkChl.getId());
        if (this.id == null || this.dsSpkChl == null || this.appearance == null)
        {
            throw new IllegalArgumentException("cannot be null");
        }
    }
    
    public UID getId()
    {
        return this.id;
    }

    public UserSpeakerChannel getDsSpeaker()
    {
        return this.dsSpkChl;
    }

    public void setCall(final SpeakerCall speakerCall)
    {
        this.speakerCall = speakerCall;
    }

    public SpeakerCall getCall()
    {
        return this.speakerCall;
    }

    public ButtonAppearance getAppearance()
    {
        return this.appearance;
    }
}
