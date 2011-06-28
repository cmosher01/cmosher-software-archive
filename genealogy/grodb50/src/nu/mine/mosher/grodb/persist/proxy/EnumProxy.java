/*
 * Created on May 19, 2006
 */
package nu.mine.mosher.grodb.persist.proxy;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

@Persistent(proxyFor=Enum.class)
public class EnumProxy implements PersistentProxy<Enum>
{
	@KeyField(1) private String name;
	@KeyField(2) private String clas;

	private EnumProxy()
	{
	}

	public void initializeProxy(final Enum enm)
	{
		this.name = enm.toString();
		this.clas = enm.getDeclaringClass().getCanonicalName();
	}

	public Enum convertProxy()
	{
		try
		{
			return Enum.valueOf((Class<Enum>)Class.forName(this.clas),this.name);
		}
		catch (ClassNotFoundException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
