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
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.util.StringUtils;

/**
 * Provides a simple interface to Velocity. This class
 * is a thin (but opaque) wrapper around VelocityEngine.
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
    public VelocityWrapper(final Properties props) throws VelocityException
    {
        try
    	{
			AccessController.doPrivileged(new PrivilegedExceptionAction()
			{
				public Object run() throws Exception
				{
					getVelocityEngine().init(props);
					return null;
				}
			});
    	}
    	catch (PrivilegedActionException e)
    	{
			throw wrapInVelocityException(e.getCause());
    	}
    }

	protected VelocityEngine getVelocityEngine()
	{
		return this.velocity;
	}

    /**
     * Parses a template, passing it a context, and writes
     * the result to a writer.
     * Same as <code>VelocityEngine.mergeTemplate</code>, but this method
     * only throws VelocityException exceptions, and doesn't
     * return a boolean.
     * @param templateName
     * @param context
     * @param writer
     * @throws VelocityException any exceptions are wrapped in a VelocityException
     */
    public void mergeTemplate(final String templateName, final Context context, final Writer writer) throws VelocityException
    {
		try
		{
			AccessController.doPrivileged(new PrivilegedExceptionAction()
			{
				public Object run() throws Exception
				{
					StringUtils.normalizePath(templateName);
					if (!getVelocityEngine().mergeTemplate(templateName,context,writer))
					{
						throw new VelocityException("error calling VelocityEngine.mergeTemplate");
					}
					return null;
				}
			});
		}
		catch (PrivilegedActionException e)
		{
			throw wrapInVelocityException(e.getCause());
		}
    }

    /**
     * Same as the other evaluate method, but the parameters
     * are in a somewhat more logical and consistent order.
     * @param reader
     * @param nameTemplate
     * @param context
     * @param writer
     * @throws VelocityException any exceptions are wrapped in a VelocityException
     */
    public void evaluate(Reader reader, String templateName, Context context, Writer writer) throws VelocityException
    {
        evaluate(context,writer,templateName,reader);
    }

    /**
     * Parses a template (given as a Reader), passing it a context,
     * and writes the results to a writer.
     * Same as <code>VelocityEngine.evaluate</code>, but this method
     * only throws VelocityException exceptions, and doesn't
     * return a boolean.
     * @param context
     * @param writer
     * @param nameTemplate
     * @param reader
     * @throws VelocityException any exceptions are wrapped in a VelocityException
     */
    public void evaluate(final Context context, final Writer writer, final String templateName, final Reader reader) throws VelocityException
    {
		try
		{
			AccessController.doPrivileged(new PrivilegedExceptionAction()
			{
				public Object run() throws Exception
				{
					if (!getVelocityEngine().evaluate(context,writer,templateName,reader))
					{
						throw new VelocityException("error calling VelocityEngine.evaluate");
					}
					return null;
				}
			});
		}
		catch (PrivilegedActionException e)
		{
			throw wrapInVelocityException(e.getCause());
		}
    }

    /**
     * Wraps an exception in a VelocityException.
     * If e is a MethodInvocationException, then fix it up so
     * its cause is initialized to its wrappedThrowable.
     * If e is any VelocityException, then this method just returns it.
     * @param e the exception to be wrapped
     * @return VelocityException with its cause initialized
     * to the given exception e.
     */
    public static VelocityException wrapInVelocityException(Throwable e)
    {
		if (e instanceof VelocityException)
		{
			// TODO If Velocity fixes MethodInvocationException to use initCause, then remove this code.
			if (e instanceof MethodInvocationException)
			{
				e.initCause(((MethodInvocationException)e).getWrappedThrowable());
			}
			return (VelocityException)e;
		}

        VelocityException ve = new VelocityException(e.getMessage());
        ve.initCause(e);
        return ve;
    }

    /**
     * Sets a properties object with some
     * useful defaults for Velocity.
     * The properties will use the global logger
     * for logging, set input and output encodings
     * to UTF-8, and clear the VelociMacro library name.
     * 
     * @param props the Properties set to be added to
     */
    public static void getDefaultProperties(Properties props)
    {
        // use our own LogSystem (VelocityLogger)
        props.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,VelocityLogger.class.getName());

        props.setProperty(VelocityEngine.INPUT_ENCODING,"UTF-8");
        props.setProperty(VelocityEngine.OUTPUT_ENCODING,"UTF-8");

        // This just prevents a warning message about no macro library
        props.setProperty(VelocityEngine.VM_LIBRARY,"");
    }



    /**
     * Convenience method that parses a template (given
     * as a File specification), with a given context,
     * and writes the result to a writer. It uses the
     * default properties to initialize Velocity.
     * @param inputTemplate
     * @param context
     * @param out
     * @throws VelocityException any exceptions are wrapped in a VelocityException
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

        Properties props = new Properties();
        VelocityWrapper.getDefaultProperties(props);
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
     * @throws VelocityException any exceptions are wrapped in a VelocityException
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
