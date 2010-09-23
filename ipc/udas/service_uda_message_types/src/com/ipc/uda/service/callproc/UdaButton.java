/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;


import com.ipc.ds.entity.dto.Button;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.UID;

/**
 * @author mordarsd
 *
 */
public abstract class UdaButton
{
    private final UID id;
    private final Button dsButton;

    // TODO do we need to have fixed boolean here?
    private boolean isFixed;

    UdaButton(final Button dsButton)
    {
        this.dsButton = dsButton;

        // optimization: calculate ID in the constructor:
        this.id = UID.fromDataServicesID(this.dsButton.getId());

        if (this.id == null || this.dsButton == null)
        {
            throw new IllegalArgumentException("cannot be null");
        }
    }    
 
    public UID getId()
    {
        return this.id;
    }

    public Button getDsButton()
    {
        return this.dsButton;
    }

    /**
     * @return the isFixed
     */
    public boolean isFixed()
    {
        return this.isFixed;
    }

    /**
     * @param isFixed the isFixed to set
     */
    public void setFixed(boolean isFixed)
    {
        this.isFixed = isFixed;
    }

    public abstract void press();

    public abstract Optional<ButtonPressCall> getCall();
    public abstract Optional<ButtonAppearance> getAppearance();

    @Override
    public boolean equals(final Object object)
    {
        if (!(object instanceof UdaButton))
        {
            return false;
        }
        final UdaButton that = (UdaButton)object;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode()
    {
        return this.id.hashCode();
    }
}
