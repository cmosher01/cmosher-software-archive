package com.surveysampling.testwarjee6;
/*
To get this to work for a Maven-built WAR file,
add this to the war project's pom.xml file:

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-war-plugin</artifactId>
				<version>2.1.1</version>
				<configuration>
					<archive>
						<manifest>
							<addDefaultImplementationEntries>true</addDefaultImplementationEntries>
							<addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>
						</manifest>
					</archive>
				</configuration>
			</plugin>
		</plugins>
	</build>

 */


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Initializes {@link WebAppVersion} upon web application startup.
 * 
 * @author christopher_mosher
 */
@WebListener
public final class WebAppVersionInitializer implements ServletContextListener
{
	/**
	 * Tries to read the implementation version from this servlet's manifest
	 * file. Called by the container when this web application has just started
	 * up.
	 * 
	 * @param e
	 *            event information
	 */
	@Override
	public void contextInitialized(final ServletContextEvent e)
	{
		final WebAppVersion w = new WebAppVersion(e.getServletContext());

		w.initVersion();
	}



	/**
	 * Invalidates the version number stored during
	 * {@link WebAppVersionInitializer#contextInitialized}.
	 * 
	 * @param e
	 *            event information
	 */
	@Override
	public void contextDestroyed(@SuppressWarnings("unused") final ServletContextEvent e)
	{
		WebAppVersion.destroyVersion();
	}
}
