package com.surveysampling.testwarjee6;

import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;


/**
 * Reads (and caches in a static String) the version number of a web application
 * from its manifest file.
 * 
 * @author christopher_mosher
 */
public final class WebAppVersion
{
	/**
	 * Caches the version number. Default is empty string.
	 */
	private static String version = "";



	/**
	 * Gets the application version number.
	 * 
	 * @return version number from manifest, or empty string if cannot get it
	 */
	public static synchronized String getVersion()
	{
		return WebAppVersion.version;
	}

	/**
	 * Sets the application version number. If this method is not called, the
	 * version defaults to the empty string. Passing in <code>null</code> is
	 * equivalent to passing in an empty string.
	 * 
	 * @param version
	 *            version number to set
	 */
	private static synchronized void setVersion(final String version)
	{
		WebAppVersion.version = (version == null) ? "" : version;
	}



	private final ServletContext ctx;

	/**
	 * Initializes this <code>WebAppVersion</code> to use the given servlet
	 * context.
	 * 
	 * @param ctx
	 *            the servlet's context to read the version number from and
	 *            write log messages to. If null, then <code>initVersion</code>
	 *            is a no-op.
	 */
	WebAppVersion(final ServletContext ctx)
	{
		this.ctx = ctx;
	}

	/**
	 * Reads this servlet's version number from the manifest file and caches it.
	 */
	void initVersion()
	{
		if (this.ctx == null)
		{
			/*
			 * We were given a null context, so we can't do anything but fail
			 * gracefully.
			 */
			return;
		}



		setVersion(getVersionStringFrom(loadManifestIfPossible(openManifestStream())));



		logVersionToContext();
	}


	private String getVersionStringFrom(final Manifest manifest)
	{
		final StringBuilder sb = new StringBuilder(64);

		final String versSpecOrNull = manifest.getMainAttributes().getValue(Attributes.Name.SPECIFICATION_VERSION);
		final String versImplOrNull = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);

		if (versSpecOrNull != null)
		{
			sb.append(versSpecOrNull);
		}
		if (versSpecOrNull != null && versImplOrNull != null)
		{
			sb.append("_");
		}
		if (versImplOrNull != null)
		{
			sb.append(versImplOrNull);
		}

		return sb.toString();
	}

	static synchronized void destroyVersion()
	{
		setVersion(getVersion() + " (ZOMBIE)");
	}

	/**
	 * Logs this servlet's version number to this servlet context's logging
	 * mechanism, if the version number is not empty.
	 * 
	 * @param ctx
	 */
	private void logVersionToContext()
	{
		final String v = getVersion();

		if (!v.isEmpty())
		{
			log(this.ctx.getContextPath() + ": " + Attributes.Name.IMPLEMENTATION_VERSION + ": " + v);
		}
	}

	/**
	 * Reads a {@link Manifest} from the given {@link InputStream}. If any error
	 * happens, this method returns an empty <code>Manifest</code> object.
	 * 
	 * @param meta_inf_manifest_mf
	 *            <code>InputStream</code> to read the manifest from (can be
	 *            null)
	 * @return <code>Manifest</code> (always consistent, never null)
	 */
	private Manifest loadManifestIfPossible(final InputStream meta_inf_manifest_mf)
	{
		if (meta_inf_manifest_mf == null)
		{
			/* no input available, so return empty Manifest */
			return new Manifest();
		}

		try
		{
			return new Manifest(meta_inf_manifest_mf);
		}
		catch (final Throwable e)
		{
			/*
			 * If anything goes wrong, log and ignore it, and return a
			 * consistent empty Manifest.
			 */
			log("Error reading from file.", e);
			return new Manifest();
		}
		finally
		{
			/* always make sure the reader is closed */
			try
			{
				meta_inf_manifest_mf.close();
			}
			catch (final Throwable e)
			{
				/*
				 * Log and ignore any exception, so it doesn't mask any original
				 * exception.
				 */
				log("Error closing reader.", e);
			}
		}
	}

	/**
	 * Opens an {@link InputStream} that reads the given servlet's manifest
	 * file.
	 * 
	 * @return <code>InputStream</code> that reads from <code>ctx</code>'s
	 *         manifest file. If anything goes wrong, returns <code>null</code>.
	 */
	private InputStream openManifestStream()
	{
		try
		{
			final URL urlManifest = this.ctx.getResource("/" + JarFile.MANIFEST_NAME);
			if (urlManifest == null)
			{
				log("Error getting manifest file: " + JarFile.MANIFEST_NAME);
				return null;
			}

			log("Will attempt to read manifest file: " + urlManifest);

			return urlManifest.openStream();
		}
		catch (final Throwable e)
		{
			log("Error reading from manifest file.", e);
			return null;
		}
	}

	private void log(final String message)
	{
		this.ctx.log(message);
	}

	private void log(final String message, final Throwable e)
	{
		this.ctx.log(message, e);
	}
}
