/*
 * Created on Apr 16, 2004
 */
package nu.mine.mosher.velocity;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
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

    public void evaluate(Context context, Writer writer, String logTag, Reader reader) throws VelocityException
    {
        try
        {
            velocity.evaluate(context, writer, logTag, reader);
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
