/*
 * Created on Apr 16, 2004
 */
package nu.mine.mosher.velocity;

import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;

/**
 * TODO
 */
public class VelocityWrapper
{
    private final VelocityEngine velocity = new VelocityEngine();

    public VelocityWrapper(Properties props) throws VelocityException
    {
        try
        {
            velocity.init(props);
        }
        catch (VelocityException e)
        {
            throw e;
        }
        catch (Throwable e)
        {
            throw wrapInVelocityException(e);
        }
    }

    public static VelocityException wrapInVelocityException(Throwable e)
    {
        VelocityException ve = new VelocityException(e.getMessage());
        ve.initCause(e);
        return ve;
    }

    public static Properties getDefaultProperties()
    {
        Properties props = new Properties();

        // use our own LogSystem (VelocityLogger)
        props.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,VelocityLogger.class.getClass().getName());

        // This just prevents a warning message about no macro library
        props.setProperty(VelocityEngine.VM_LIBRARY,"");

        return props;
    }
}
