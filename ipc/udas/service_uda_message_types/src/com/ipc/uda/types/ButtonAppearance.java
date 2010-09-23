/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.types;

/**
 * Represents an appearance of a button, identified by AOR and appearance number.
 * @author mosherc
 */
public class ButtonAppearance
{
    private final String aor;
    private final int appearance;

    // REVIEW 0 Add javadocs for public methods
    // REVIEW 0 Add Unit Test
    
    @SuppressWarnings("synthetic-access")
    public ButtonAppearance(final String aor, final int appearance) throws Invalid
    {
        this.aor = aor;
        this.appearance = appearance;

        if (this.aor == null || this.aor.isEmpty() || this.appearance <= 0)
        {
            throw new Invalid(aor,appearance);
        }
    }

    public String getAor()
    {
        return this.aor;
    }

    public int getAppearance()
    {
        return this.appearance;
    }

    @Override
    public boolean equals(final Object object)
    {
        if (!(object instanceof ButtonAppearance))
        {
            return false;
        }
        final ButtonAppearance that = (ButtonAppearance)object;
        return this.aor.equals(that.aor) && this.appearance==that.appearance;
    }

    @Override
    public int hashCode()
    {
        int hash = 17;
        hash *= 37;
        hash += this.aor.hashCode();
        hash *= 37;
        hash += this.appearance;
        return hash;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("ButtonAppearance: ");
        sb.append(this.aor);
        if (1 < this.appearance)
        {
            sb.append("(appearance ");
            sb.append(this.appearance);
            sb.append(")");
        }
        return sb.toString();
    }

    public static class Invalid extends Throwable
    {
        private Invalid(final String aor, final int appearance)
        {
            super("Invalid AOR/appearance: "+aor+"/"+appearance);
        }
    }
}
