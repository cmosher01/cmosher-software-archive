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
        String prop = "robjInteger";
        String[] val = {"99","23"};

        setProperty(some, prop, val);

        System.out.println(some.getRobjInteger()[0].toString());
        System.out.println(some.getRobjInteger()[1].toString());
    }

    public static void setProperty(Object bean, String property, String value)
        throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), property);
        Object v = convertValue(value, pd.getPropertyType());
        setProperty(bean, pd, v);
    }

    public static void setProperty(Object bean, String property, String[] value)
        throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), property);
        Object v = getConvertedValue(value, pd.getPropertyType());
        setProperty(bean, pd, v);
    }

    public static Object convertValue(String value, Class classProp)
        throws NegativeArraySizeException, IllegalArgumentException, IntrospectionException
    {
        if (classProp.isArray())
        {
            throw new IllegalArgumentException("Cannot set an array property to a scalar value.");
        }
        return convertScalar(value,classProp);
    }

    public static Object getConvertedValue(String[] value, Class classProp)
        throws NegativeArraySizeException, IllegalArgumentException, IntrospectionException
    {
        if (!classProp.isArray())
        {
            throw new IllegalArgumentException("Cannot set a scalar property to an array of values.");
        }
        return convertArray(value, classProp);
    }

    public static void setProperty(Object bean, PropertyDescriptor pd, Object value)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Method wr = pd.getWriteMethod();
        if (wr == null)
        {
            throw new IllegalAccessException("Cannot write property " + pd.getName());
        }
        
        wr.invoke(bean, new Object[] { value });
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
