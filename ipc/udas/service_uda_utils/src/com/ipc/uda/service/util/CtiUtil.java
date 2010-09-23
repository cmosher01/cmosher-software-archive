/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.util;



import com.ipc.va.cti.CtiException;
import com.ipc.va.cti.ResultStatus;
import com.ipc.va.cti.ResultStatusType;



/**
 * @author mosherc
 */
public final class CtiUtil
{
    CtiUtil()
    {
        throw new IllegalStateException();
    }

    /**
     * Checks the given result status as returned from a CTI method.
     * If the status is not SUCCESS, then this method throws an
     * exception; otherwise it does nothing. If <code>result</code> is null,
     * this method just returns. If <code>result.getResultStatus()</code>
     * returns null, this method just returns.
     * @param result the status to check
     * @throws CtiException if the result status is not SUCCESS
     */
    public static void checkCtiResultStatus(final ResultStatus result) throws CtiException
    {
        if (result == null)
        {
            return;
        }

        final ResultStatusType status = result.getResultStatus();
        if (status == null)
        {
            return;
        }

        if (!status.equals(ResultStatusType.SUCCESS))
        {
            throw new CtiException();
        }
    }

}
