/*
 * Created on Apr 16, 2004
 */
package nu.mine.mosher.velocity;

import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
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

    public void evaluate(Reader reader, String nameTemplate, Context context, Writer writer) throws VelocityException
    {
        evaluate(context,writer,nameTemplate,reader);
    }

    public void evaluate(Context context, Writer writer, String nameTemplate, Reader reader) throws VelocityException
    {
        boolean ok = false;
        try
        {
            ok = velocity.evaluate(context, writer, nameTemplate, reader);
        }
        catch (Throwable e)
        {
            throw wrapInVelocityException(e);
        }
        if (!ok)
        {
            throw new VelocityException("error calling VelocityEngine.evaluate");
        }
    }

    public void mergeTemplate(String templateName, Context context, Writer writer) throws VelocityException
    {
        boolean ok = false;
        try
        {
            ok = velocity.mergeTemplate(templateName, context, writer);
        }
        catch (Throwable e)
        {
            throw wrapInVelocityException(e);
        }
        if (!ok)
        {
            throw new VelocityException("error calling VelocityEngine.mergeTemplate");
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
        props.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, VelocityLogger.class.getName());

        props.setProperty(VelocityEngine.INPUT_ENCODING,"UTF-8");
        props.setProperty(VelocityEngine.OUTPUT_ENCODING,"UTF-8");

        // This just prevents a warning message about no macro library
        props.setProperty(VelocityEngine.VM_LIBRARY, "");

        return props;
    }
}
