/*
 * Created on Apr 16, 2004
 */
package nu.mine.mosher.velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;

/**
 * Provides a simple interface to Velocity.
 * 
 * @author Chris Mosher
 */
public class VelocityWrapper
{
    private final VelocityEngine velocity = new VelocityEngine();



    /**
     * Initializes Velocity with a given set of properties.
     * Before calling this constructor, call the static
     * method <code>getDefaultProperties</code> to get a
     * Properties object filled with some useful default values,
     * then fill in any overriding properties desired, then
     * pass it to this constructor.
     * 
     * @param props properties to initialize Velocity with
     * @throws VelocityException any exceptions are wrapped in a VelocityException
     */
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

    /**
     * Parses a template, passing it a context, and writes
     * the result to a writer.
     * Same as VelocityEngine.mergeTemplate, but this method
     * only throws VelocityException exceptions, and doesn't
     * return a boolean.
     * @param templateName
     * @param context
     * @param writer
     * @throws VelocityException
     */
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

    /**
     * Same as the other evaluate method, but the parameters
     * are in a somewhat more logical and consistent order.
     * @param reader
     * @param nameTemplate
     * @param context
     * @param writer
     * @throws VelocityException
     */
    public void evaluate(Reader reader, String nameTemplate, Context context, Writer writer) throws VelocityException
    {
        evaluate(context,writer,nameTemplate,reader);
    }

    /**
     * Parses a template (given as a Reader), passing it a context,
     * and writes the results to a writer.
     * Same as VelocityEngine.evaluate, but this method
     * only throws VelocityException exceptions, and doesn't
     * return a boolean.
     * @param context
     * @param writer
     * @param nameTemplate
     * @param reader
     * @throws VelocityException
     */
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

    /**
     * Wraps an exception in a VelocityException.
     * @param e the exception to be wrapped
     * @return VelocityException with its cause initialized
     * to the given exception e.
     */
    public static VelocityException wrapInVelocityException(Throwable e)
    {
        VelocityException ve = new VelocityException(e.getMessage());
        ve.initCause(e);
        return ve;
    }

    /**
     * Gets a properties object initialized with some
     * useful defaults for Velocity.
     * The properties will use the global logger
     * for logging, set input and output encodings
     * to UTF-8, and clear the VelociMacro library name.
     * @return
     */
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



    /**
     * Convenience method that parses a template (given
     * as a File specification), with a given context,
     * and writes the result to a writer. It uses the
     * default properties to initialize Velocity.
     * @param inputTemplate
     * @param context
     * @param out
     * @throws VelocityException
     */
    public static void merge(File inputTemplate, Context context, Writer out) throws VelocityException
    {
        try
        {
            inputTemplate = inputTemplate.getAbsoluteFile().getCanonicalFile();
        }
        catch (IOException e)
        {
            throw wrapInVelocityException(e);
        }

        Properties props = VelocityWrapper.getDefaultProperties();
        props.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH,inputTemplate.getParentFile().getAbsolutePath());

        VelocityWrapper velocity = new VelocityWrapper(props);

        velocity.mergeTemplate(inputTemplate.getName(),context,out);
    }

    /**
     * Convenience method that parses a template (given
     * as a File specification), with a given context,
     * and writes the result to a File. It uses the
     * default properties to initialize Velocity.
     * @param inputTemplate
     * @param context
     * @param outputFile
     * @throws VelocityException
     */
    public static void merge(File inputTemplate, Context context, File outputFile) throws VelocityException
    {
        BufferedWriter out = null;
        try
        {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
            merge(inputTemplate,context,out);
        }
        catch (FileNotFoundException e)
        {
            throw wrapInVelocityException(e);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
