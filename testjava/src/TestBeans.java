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

        SomeBean some = new SomeBean();
        String prop = "ABoolean";
        String val = "true";

        setProperty(some, prop, val);

        System.out.println(some.isABoolean() ? "yes" : "no");
    }

    public static void setProperty(Object bean, String property, String value)
        throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        setProperty(bean,property,new String[] { value });
    }

    public static void setProperty(Object bean, String property, String[] value)
        throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        BeanInfo bi = getBeanInfo(bean);

        PropertyDescriptor[] rpd = bi.getPropertyDescriptors();
        if (rpd == null)
        {
            throw new IntrospectionException("can't get property descriptors for bean");
        }

        Map mapPDs = buildPropertyDescriptorMap(rpd);
        if (!mapPDs.containsKey(property))
        {
            throw new IntrospectionException("can't get property descriptor");
        }

        PropertyDescriptor pd = (PropertyDescriptor)mapPDs.get(property);

        Class classProp = pd.getPropertyType();

        Object v;
        if (classProp.isArray())
        {
            classProp = classProp.getComponentType();
            PropertyEditor ed = PropertyEditorManager.findEditor(classProp);
            if (ed == null)
            {
                throw new IntrospectionException("can't get property editor");
            }
            Object[] rval = (Object[])Array.newInstance(classProp,value.length);
            for (int i = 0; i < value.length; ++i)
            {
                String s = value[i];
                ed.setAsText(s);
                rval[i] = ed.getValue();
            }
            v = rval;
        }
        else
        {
            PropertyEditor ed = PropertyEditorManager.findEditor(classProp);
            if (ed == null)
            {
                throw new IntrospectionException("can't get property editor");
            }
            ed.setAsText(value[0]);
            v = ed.getValue();
        }

        Method wr = pd.getWriteMethod();
        wr.invoke(bean, new Object[] { v });
    }

    public static BeanInfo getBeanInfo(Object bean) throws IntrospectionException
    {
        BeanInfo bi = Introspector.getBeanInfo(bean.getClass());
        if (bi == null)
        {
            throw new IntrospectionException("Cannot get BeanInfo for bean class "+bean.getClass().getName());
        }
        return bi;
    }

    public static HashMap buildPropertyDescriptorMap(PropertyDescriptor[] rpd)
    {
        HashMap map = new HashMap();
        for (Iterator i = Arrays.asList(rpd).iterator(); i.hasNext();)
        {
            PropertyDescriptor pd = (PropertyDescriptor)i.next();
            map.put(pd.getName(),pd);
        }
        return map;
    }

    public static void showInt(int i)
    {
        System.out.println(i);
    }
}
