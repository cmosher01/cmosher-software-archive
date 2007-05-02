/*
 * Created on May 28, 2004
 */
package com.surveysampling.bulkemailer.mta;

import java.io.Serializable;

/**
 * Represents (some of) the persistent state of an <code>Mta</code>.
 * The attributes were pulled out of <code>Mta</code> into this
 * separate class for ease of serailizing and cloning.
 */
public final class MtaState implements Serializable, Cloneable
{
    /**
     * protocol; defaults to smtp
     */
    public String mScheme = "smtp";
    /**
     * host
     */
    public String mHost;
    /**
     * port on host
     */
    public int mPort = 25;
    /**
     * send rate
     */
    public int mRate;
    /**
     * time out in ms
     */
    public int mTimeout = 120000;
    /**
     * tier; default 1
     */
    public int mTier = 1;
    /**
     * on-hold; default true
     */
    public boolean mOnHold = true;    

    /**
     * Creates a copy of this MtaState.
     * @return the copy
     */
    @Override
    public Object clone()
    {
        MtaState clon = null;
        try
        {
            clon = (MtaState)super.clone();
        }
        catch (CloneNotSupportedException cantHappen)
        {
            throw new RuntimeException(cantHappen);
        }
        return clon;
    }
}
