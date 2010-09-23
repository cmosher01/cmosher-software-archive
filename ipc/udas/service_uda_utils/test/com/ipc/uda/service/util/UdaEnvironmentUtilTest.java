/**
 * 
 */
package com.ipc.uda.service.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author mosherc
 *
 */
public class UdaEnvironmentUtilTest
{
    @Test(expected=RuntimeException.class)
    public void cannotInstantiate()
    {
        new UdaEnvironmentUtil();
    }

    @Test(expected=UdaEnvironmentException.class)
    public void timerManagerNotAvailableBecauseNotRunningWithinContainer()
    {
        UdaEnvironmentUtil.getTimerManager();
    }

}
