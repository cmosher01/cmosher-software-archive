/*
 * Created on Apr 14, 2004
 */
package nu.mine.mosher.servlet;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nu.mine.mosher.beans.BeanUtil;
import nu.mine.mosher.beans.DataTypeConversionException;
import nu.mine.mosher.beans.PropertyEditorNotFoundException;
import nu.mine.mosher.playvel.FormField;

/**
 * @author Chris Mosher
 */
public final class Servlets
{
    private Servlets() throws UnsupportedOperationException
    {
    	throw new UnsupportedOperationException();
    }

    public static void parseParameters(Object bean, Map mapParam, List rfieldResult)
        throws
            SecurityException,
            ExceptionInInitializerError,
            PropertyEditorNotFoundException,
            IllegalAccessException,
            IntrospectionException
	{
		/*
		 * Go through each param and set appropriate
		 * bean property for that param.
		 */
		for (Iterator i = mapParam.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry param = (Map.Entry)i.next();
			Throwable ex = null;
			try
            {
                BeanUtil.setProperty(bean,(String)param.getKey(),(String[])param.getValue());
            }
            catch (InvocationTargetException e)
            {
            	ex = e;
            }
            catch (DataTypeConversionException e)
            {
				ex = e;
            }
            if (ex != null)
            {
            	ex.printStackTrace();
            }
			rfieldResult.add(new FormField((String)param.getKey(),((String[])param.getValue())[0],ex));
		}
	}

	public static void buildFieldMap(Object bean, List names, Map fields) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		for (Iterator i = names.iterator(); i.hasNext();)
        {
            String name = (String)i.next();
			PropertyDescriptor desc = BeanUtil.getPropertyDescriptor(bean.getClass(),name);
			Method read = desc.getReadMethod();
			fields.put(name,new FormField(name,read.invoke(bean,null)));
        }
	}
}
