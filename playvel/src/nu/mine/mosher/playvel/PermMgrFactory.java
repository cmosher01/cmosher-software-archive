package nu.mine.mosher.playvel;

import java.io.IOException;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletContext;

public class PermMgrFactory
{
	private final Properties props = new Properties();

	public PermMgrFactory(ServletContext ctx) throws IOException
	{
		props.load(ctx.getResourceAsStream("/WEB-INF/jdo.properties"));
	}

	protected PersistenceManager getPM()
	{
		return JDOHelper.getPersistenceManagerFactory(props).getPersistenceManager();
	}
}
