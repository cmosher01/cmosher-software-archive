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
        PropertyDescriptor pd = getPropertyDescriptor(bean, property);

        Class classProp = pd.getPropertyType();

        Object v;
        if (classProp.isArray())
        {
            classProp = classProp.getComponentType();
            v = convertArray(value, getPropertyEditor(classProp), classProp);
        }
        else
        {
            v = convert(value[0], getPropertyEditor(classProp));
        }

        Method wr = pd.getWriteMethod();
        if (wr == null)
        {
            throw new IllegalAccessException("Cannot write property " + property);
        }

        wr.invoke(bean, new Object[] { v });
    }

    public static PropertyDescriptor getPropertyDescriptor(Object bean, String property) throws IntrospectionException
    {
        Map mapPDs = getPropertyDescriptors(getBeanInfo(bean));
        if (!mapPDs.containsKey(property))
        {
            throw new IntrospectionException("Cannot find property descriptor for " + property);
        }
        
        return (PropertyDescriptor)mapPDs.get(property);
    }

    public static Object[] convertArray(String[] value, PropertyEditor ed, Class classProp)
        throws NegativeArraySizeException, IllegalArgumentException
    {
        Object[] rval = (Object[])Array.newInstance(classProp, value.length);
        for (int i = 0; i < value.length; ++i)
        {
            rval[i] = convert(value[i], ed);
        }
        return rval;
    }

    public static Object convert(String value, PropertyEditor ed) throws IllegalArgumentException
    {
        ed.setAsText(value);
        return ed.getValue();
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

    public static BeanInfo getBeanInfo(Object bean) throws IntrospectionException
    {
        BeanInfo bi = Introspector.getBeanInfo(bean.getClass());
        if (bi == null)
        {
            throw new IntrospectionException("Cannot get BeanInfo for bean class " + bean.getClass().getName());
        }

        return bi;
    }
}
