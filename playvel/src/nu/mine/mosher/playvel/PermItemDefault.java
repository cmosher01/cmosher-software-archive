package nu.mine.mosher.playvel;

import java.util.ArrayList;
import java.util.Iterator;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

public class PermItemDefault implements PermItem
{
    public String getName()
    {
    	return "&nbsp;";
    }

    public String getId()
    {
    	Object oid = JDOHelper.getObjectId(this);
    	if (oid == null)
    	{
    		return "";
    	}

		return oid.toString();
    }

    public ArrayList getAll(PersistenceManager pm)
    {
    	ArrayList r = new ArrayList();
		for (Iterator i = pm.getExtent(this.getClass(),true).iterator(); i.hasNext(); )
		{
			r.add(i.next());
		}
        return r;
    }
}
