/*
 * Created on Apr 16, 2004
 */
package nu.mine.mosher.velocity;

import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;

/**
 * TODO
 */
public class VelocityWrapper
{
    private final VelocityEngine velocity = new VelocityEngine();

    public VelocityWrapper(Properties props)
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
}
