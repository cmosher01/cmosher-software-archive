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
}
