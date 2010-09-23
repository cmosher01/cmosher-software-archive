/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;

import com.ipc.ds.entity.dto.Button;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.ButtonAppearance;

/**
 * @author mordarsd
 *
 */
public class CallBasedButton extends UdaButton
{

    private ButtonPressCall call;
    private final ButtonAppearance appearance;
    
    /**
     * 
     * @param id
     * @param call
     */
    CallBasedButton(final Button dsButton, final ButtonAppearance appearance)
    {
        super(dsButton);
        this.appearance = appearance;
    }

    /* (non-Javadoc)
     * @see com.ipc.uda.service.callproc.Button#press()
     */
    @Override
    public void press()
    {
        this.call.udacButtonPressed();
    }

    @Override
    public Optional<ButtonPressCall> getCall()
    {
        return new Optional<ButtonPressCall>(this.call);
    }

    @Override
    public Optional<ButtonAppearance> getAppearance()
    {
        return new Optional<ButtonAppearance>(this.appearance);
    }
    
    public void setCall(final ButtonPressCall call)
    {
        this.call = call;
    }
    
    
    
}
