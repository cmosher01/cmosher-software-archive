/*
 * Created on Apr 16, 2004
 */
package nu.mine.mosher.velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
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

    public void mergeTemplate(String templateName, Context context, Writer writer) throws VelocityException
    {
        try
        {
            if (!velocity.mergeTemplate(templateName,context,writer))
            {
                throw new VelocityException("error calling VelocityEngine.mergeTemplate");
            }
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
        try
        {
            if (!velocity.evaluate(context,writer,nameTemplate,reader))
            {
                throw new VelocityException("error calling VelocityEngine.evaluate");
            }
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
        props.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,VelocityLogger.class.getName());

        props.setProperty(VelocityEngine.INPUT_ENCODING,"UTF-8");
        props.setProperty(VelocityEngine.OUTPUT_ENCODING,"UTF-8");

        // This just prevents a warning message about no macro library
        props.setProperty(VelocityEngine.VM_LIBRARY,"");

        return props;
    }



    public static void merge(File inputTemplate, Context context, Writer out) throws VelocityException
    {
        inputTemplate = inputTemplate.getAbsoluteFile().getCanonicalFile();

        Properties props = VelocityWrapper.getDefaultProperties();
        props.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH,inputTemplate.getParentFile().getAbsolutePath());

        VelocityWrapper velocity = new VelocityWrapper(props);

        velocity.mergeTemplate(inputTemplate.getName(),context,writer);
    }
}
