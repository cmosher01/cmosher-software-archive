/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;

import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.UID;
import com.ipc.va.cti.ConnectionID;

/**
 * @author mosherc
 */
public class PrivateWireNonMockableActions
{
	protected final ButtonAppearance appearance;
	protected final UdaButton udaButton;
    protected String digits;
    protected String cliName;
	protected String cliNumber;

    public PrivateWireNonMockableActions(final ButtonAppearance appearance, final UdaButton udaButton)
    {
        this.appearance = appearance;
        this.udaButton = udaButton;
        if (this.appearance == null && this.udaButton == null)
        {
            throw new IllegalArgumentException("AOR/Appearance/UdaButton cannot be null");
        }
    }

    // an identifying name, for use by the log file
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(this.appearance);

        return sb.toString();
    }

    public void autoUdacSeize(final PrivateWireFsmContext context)
    {
        context.udacSeize();
    }

    public void setDigits(final String digits)
    {
        this.digits = digits;
    }

    public void setCli(final String name, final String number)
    {
        this.cliName = name;
        this.cliNumber = number;
    }
}
