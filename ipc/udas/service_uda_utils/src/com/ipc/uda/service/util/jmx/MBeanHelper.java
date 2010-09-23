/**
 * 
 */
package com.ipc.uda.service.util.jmx;



/**
 * @author mordarsd
 * 
 */
public final class MBeanHelper
{
    static final String JMX_ENABLED_KEY = "com.ipc.uda.service.jmx.Enabled";
    private static final boolean JMX_ENABLED = Boolean.getBoolean(JMX_ENABLED_KEY);

    public static boolean isJMXEnabled()
    {
        return MBeanHelper.JMX_ENABLED;
    }
}
