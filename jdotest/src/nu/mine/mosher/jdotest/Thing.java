/*
 * Created on Mar 8, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.jdotest;

import java.util.List;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Thing extends NamedDefault implements Named
{
	private String name;
	private List rmmItem;

	public Thing()
	{
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
