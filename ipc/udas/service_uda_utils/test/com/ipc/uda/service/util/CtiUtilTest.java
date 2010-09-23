/**
 * 
 */
package com.ipc.uda.service.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ipc.va.cti.CtiException;
import com.ipc.va.cti.ResultStatus;
import com.ipc.va.cti.ResultStatusImpl;
import com.ipc.va.cti.ResultStatusType;

/**
 * @author mosherc
 *
 */
public class CtiUtilTest
{

    @Test
    public void testCheckCtiResultStatusSuccess() throws CtiException
    {
        final ResultStatusImpl result = new ResultStatusImpl(ResultStatusType.SUCCESS);
        CtiUtil.checkCtiResultStatus(result);
    }

    @Test(expected=CtiException.class)
    public void testCheckCtiResultStatusFailure() throws CtiException
    {
        final ResultStatusImpl result = new ResultStatusImpl(ResultStatusType.FAILURE);
        CtiUtil.checkCtiResultStatus(result);
    }

    @Test
    public void testCheckCtiResultStatusNull() throws CtiException
    {
        final ResultStatusImpl result = null;
        CtiUtil.checkCtiResultStatus(result);
    }

    @Test
    public void testCheckCtiResultStatusNullStatus() throws CtiException
    {
        final ResultStatus result = new ResultStatus()
        {
            @Override
            public ResultStatusType getResultStatus()
            {
                return null;
            }
        };
        CtiUtil.checkCtiResultStatus(result);
    }

    @Test(expected=IllegalStateException.class)
    public void testCannotInstantiate()
    {
        new CtiUtil();
    }
}
