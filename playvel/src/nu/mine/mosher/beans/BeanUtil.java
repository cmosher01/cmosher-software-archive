/*
 * Created on Apr 1, 2004
 */
package nu.mine.mosher.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Chris Mosher
 */
public final class BeanUtil
{
	private BeanUtil() throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}



    public static void setProperty(Object bean, String property, String[] value)
        throws
            SecurityException,
            ExceptionInInitializerError,
            InvocationTargetException,
            IllegalAccessException,
            IntrospectionException,
            PropertyEditorNotFoundException,
            DataTypeConversionException
	{
		PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), property);
		Class classProp = pd.getPropertyType();
		if (classProp.isArray())
		{
			setProperty(bean, pd, convertArray(value, classProp));
		}
		else
		{
			setProperty(bean, pd, convertScalar(getScalarFromArray(value), classProp));
		}
	}

    public static String getScalarFromArray(String[] value)
    {
		String val;
		if (value.length > 0)
		{
			val = value[0];
		}
		else
		{
			val = "";
		}
        return val;
    }

    public static void setPropertyScalar(Object bean, String property, String value)
        throws
            SecurityException,
            ExceptionInInitializerError,
            InvocationTargetException,
            IllegalAccessException,
            IntrospectionException,
            PropertyEditorNotFoundException,
            DataTypeConversionException
	{
		PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), property);
		setProperty(bean, pd, convertScalarSafe(value, pd.getPropertyType()));
	}

    public static void setPropertyArray(Object bean, String property, String[] value)
        throws
            SecurityException,
            ExceptionInInitializerError,
            InvocationTargetException,
            IllegalAccessException,
            IntrospectionException,
            PropertyEditorNotFoundException,
            DataTypeConversionException
	{
		PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), property);
		setProperty(bean, pd, convertArraySafe(value, pd.getPropertyType()));
	}

	public static PropertyDescriptor getPropertyDescriptor(Class forClass, String property) throws IntrospectionException
	{
		Map mapPDs = getPropertyDescriptors(Introspector.getBeanInfo(forClass));
		if (!mapPDs.containsKey(property))
		{
			throw new IntrospectionException("Cannot find property descriptor for " + property);
		}
        
		return (PropertyDescriptor)mapPDs.get(property);
	}

	public static Object convertScalarSafe(String value, Class classProp) throws PropertyEditorNotFoundException, DataTypeConversionException
	{
		if (classProp.isArray())
		{
			throw new IllegalArgumentException("Cannot set an array property to a scalar value.");
		}
		return convertScalar(value,classProp);
	}

	public static Object convertArraySafe(String[] value, Class classProp) throws PropertyEditorNotFoundException, DataTypeConversionException
	{
		if (!classProp.isArray())
		{
			throw new IllegalArgumentException("Cannot set a scalar property to an array of values.");
		}
		return convertArray(value,classProp);
	}

	/**
	 * Converts a <code>String</code> value to another class.
	 * @param value the <code>String</code> to be converted
	 * @param classProp the type to convert the value to
	 * @return the converted value, with a runtime type of classProp
	 * @throws PropertyEditorNotFoundException if a PropertyEditor for classProp cannot be found
	 */
	public static Object convertScalar(String value, Class classProp) throws PropertyEditorNotFoundException, DataTypeConversionException
	{
		return convert(value,getPropertyEditor(classProp));
	}

	/**
	 * Converts an array of <code>String</code>s to an array
	 * of <code>Object</code>s with a given runtime type.
	 * @param rValue array of <code>String</code> values to convert
	 * @param classProp the class to convert each value to
	 * @return Object[] array of converted values, with a runtime of array of classProp
	 * @throws PropertyEditorNotFoundException if a PropertyEditor for classProp cannot be found
	 */
    public static Object[] convertArray(String[] rValue, Class classProp) throws PropertyEditorNotFoundException, DataTypeConversionException
	{
		classProp = classProp.getComponentType();

		Object[] rval;
		try
		{
			rval = (Object[])Array.newInstance(classProp, rValue.length);
		}
		catch (IllegalArgumentException e)
		{
			throw new BeanUtilLogicException(e);
		}
		catch (NegativeArraySizeException e)
		{
			throw new BeanUtilLogicException(e);
		}

		PropertyEditor ed = getPropertyEditor(classProp);
		for (int i = 0; i < rValue.length; ++i)
		{
			rval[i] = convert(rValue[i],ed);
		}
		return rval;
	}

	public static Object convert(String value, PropertyEditor ed) throws DataTypeConversionException
	{
		try
		{
			ed.setAsText(value);
		}
		catch (IllegalArgumentException e)
		{
			throw new DataTypeConversionException(e);
		}
		return ed.getValue();
	}

	/**
	 * Sets a bean's property to a value.
	 * @param bean the bean whose property is to be set
	 * @param pd the descriptor of the property to be set
	 * @param value the value (of the correct runtime type) to set the property to
	 * @throws InvocationTargetException if the property's write method throws an exception
	 * @throws SecurityException if the property is read-only
	 * @throws ExceptionInInitializerError if some static initialization causes an exception
	 * @throws IllegalAccessException if the property's write method is inaccessible 
	 * @throws IntrospectionException if the property's write method is incorrectly determined by Introspector
	 */
    public static void setProperty(Object bean, PropertyDescriptor pd, Object value)
        throws InvocationTargetException, SecurityException, ExceptionInInitializerError, IllegalAccessException, IntrospectionException
	{
		Method wr = pd.getWriteMethod();
		if (wr == null)
		{
			throw new SecurityException("Cannot write property " + pd.getName());
		}

		try
        {
            wr.invoke(bean, new Object[] { value });
        }
        catch (IllegalArgumentException cause)
        {
        	/*
        	 * None of these conditions should occur.
        	 * If one does, then there's an error in
        	 * the bean introspection.
        	 */
			IntrospectionException e = new IntrospectionException(cause.getMessage());
			e.initCause(cause);
        	throw e;
        }
	}

	/**
	 * Gets a <code>PropertyEditor</code> for the given class.
	 * This is a convenience method; call this method instead of
	 * <code>PropertyEditorManager.findEditor</code> when you expect
	 * an editor to be found, and not finding one is considered
	 * an exceptional condition.
	 * 
	 * @param forClass the <code>Class</code> to get the editor for
	 * @return <code>PropertyEditor</code> for forClass
	 * @throws PropertyEditorNotFoundException if an editor cannot be found
	 * @see java.beans.PropertyEditorManager#findEditor(java.lang.Class)
	 */
	public static PropertyEditor getPropertyEditor(Class forClass) throws PropertyEditorNotFoundException
	{
		PropertyEditor ed = PropertyEditorManager.findEditor(forClass);
		if (ed == null)
		{
			throw new PropertyEditorNotFoundException(forClass);
		}
		return ed;
	}

	/**
	 * Gets the <code>PropertyDescriptor</code>s from the given
	 * <code>BeanInfo</code> object. Puts them into a map, whose keys are
	 * <code>String</code>s that are the names of the properties,
	 * and whose values are the <code>PropertyDescriptor</code>s.
	 * @param beanInfo the <code>BeanInfo</code> object
	 * @return <code>HashMap</code> of names to descriptors
	 * @throws IntrospectionException if BeanInfo.getPropertyDescriptors returns null
	 * @see java.beans.BeanInfo#getPropertyDescriptors()
	 */
	public static HashMap getPropertyDescriptors(BeanInfo beanInfo) throws IntrospectionException
	{
		PropertyDescriptor[] rpd = beanInfo.getPropertyDescriptors();
		if (rpd == null)
		{
			/*
			 * The call to getPropertyDescriptors should not return null to us.
			 * A return value of null from getPropertyDescriptors should only
			 * happen for subclasses of BeanInfo, whereby they indicate to
			 * BeanInfo that BeanInfo should do introspection to get the
			 * property descriptors.
			 */
            throw new IntrospectionException(
                "BeanInfo.getPropertyDescriptors returned null for class " + beanInfo.getBeanDescriptor().getBeanClass().getName());
		}

		HashMap map = new HashMap();
		for (Iterator i = Arrays.asList(rpd).iterator(); i.hasNext();)
		{
			PropertyDescriptor pd = (PropertyDescriptor)i.next();
			map.put(pd.getName(), pd);
		}
		return map;
	}
}
