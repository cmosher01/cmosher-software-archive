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

import com.surveysampling.beans.editors.Editors;

public class TestBeans
{
    public static void main(String[] rArgs) throws Throwable
    {
        Editors.register();

        Something some = new Something();
        String prop = "AString";
        String val = "xyz";

        setProperty(some, prop, val);

        System.out.println(some.getAString());
    }

    public static void setProperty(Object bean, String property, String value)
        throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        setProperty(bean, property, new String[] { value });
    }

    public static void setProperty(Object bean, String property, String[] value)
        throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), property);
        Object v = getConvertedValue(value, pd.getPropertyType());

        setProperty(bean, pd, v);
    }

    public static void setProperty(Object bean, PropertyDescriptor pd, Object v)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Method wr = pd.getWriteMethod();
        if (wr == null)
        {
            throw new IllegalAccessException("Cannot write property " + pd.getName());
        }
        
        wr.invoke(bean, new Object[] { v });
    }

    public static Object getConvertedValue(String[] value, Class classProp)
        throws NegativeArraySizeException, IllegalArgumentException, IntrospectionException
    {
        Object v;
        if (classProp.isArray())
        {
            v = convertArray(value, classProp);
        }
        else
        {
            if (value.length > 1)
            {
                throw new IllegalArgumentException("Cannot set a scalar property to an array of values.");
            }
            v = convertScalar(value[0], classProp);
        }
        return v;
    }

    public static PropertyDescriptor getPropertyDescriptor(Class forClass, String property) throws IntrospectionException
    {
        Map mapPDs = getPropertyDescriptors(getBeanInfo(forClass));
        if (!mapPDs.containsKey(property))
        {
            throw new IntrospectionException("Cannot find property descriptor for " + property);
        }
        
        return (PropertyDescriptor)mapPDs.get(property);
    }

    public static Object convertScalar(String value, Class classProp) throws IntrospectionException, IllegalArgumentException
    {
        PropertyEditor ed = getPropertyEditor(classProp);
        ed.setAsText(value);
        return ed.getValue();
    }

    public static Object[] convertArray(String[] value, Class classProp)
        throws NegativeArraySizeException, IllegalArgumentException, IntrospectionException
    {
        classProp = classProp.getComponentType();
        PropertyEditor ed = getPropertyEditor(classProp);
        Object[] rval = (Object[])Array.newInstance(classProp, value.length);
        for (int i = 0; i < value.length; ++i)
        {
            ed.setAsText(value[i]);
            rval[i] = ed.getValue();
        }
        return rval;
    }

    public static PropertyEditor getPropertyEditor(Class forClass) throws IntrospectionException
    {
        PropertyEditor ed = PropertyEditorManager.findEditor(forClass);
        if (ed == null)
        {
            throw new IntrospectionException("Cannot get PropertyEditor for class " + forClass.getName());
        }
        return ed;
    }

    public static HashMap getPropertyDescriptors(BeanInfo bi) throws IntrospectionException
    {
        PropertyDescriptor[] rpd = bi.getPropertyDescriptors();
        if (rpd == null)
        {
            rpd = new PropertyDescriptor[0];
        }

        HashMap map = new HashMap();
        for (Iterator i = Arrays.asList(rpd).iterator(); i.hasNext();)
        {
            PropertyDescriptor pd = (PropertyDescriptor)i.next();
            map.put(pd.getName(), pd);
        }
        return map;
    }

    public static BeanInfo getBeanInfo(Class forClass) throws IntrospectionException
    {
        BeanInfo bi = Introspector.getBeanInfo(forClass);
        if (bi == null)
        {
            throw new IntrospectionException("Cannot get BeanInfo for bean class " + forClass.getName());
        }

        return bi;
    }
}
