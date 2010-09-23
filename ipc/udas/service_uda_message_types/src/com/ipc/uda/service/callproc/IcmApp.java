/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mordarsd
 *
 */
public final class IcmApp
{
    // TODO maybe make this key be a ButtonAppearance (just hardcode appearance number to 1)???
    private final Map<String/*AOR*/,IcmP2PCall> mapAorToIntercom = new HashMap<String,IcmP2PCall>();

    // DEM: do we want to expose this map? TODO not sure
    public Map<String, IcmP2PCall> getMapAorToIntercom()
    {
        return this.mapAorToIntercom;
    }
}
