import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.surveysampling.beans.editors.Editors;

public class TestBeans
{
    public static void main(String[] rArgs) throws Throwable
    {
        Editors.register();

        SomeBean some = new SomeBean();
        String prop = "objInteger";
        String val = "34";







        setProperty(some, prop, val);



        showInt(some.getObjInteger().intValue());
    }

    public static void setProperty(Object bean, String property, String value)
        throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        BeanInfo bi = Introspector.getBeanInfo(bean.getClass());
        if (bi == null)
        {
            throw new IntrospectionException("can't get info for bean");
        }
        PropertyDescriptor[] rpd = bi.getPropertyDescriptors();
        if (rpd == null)
        {
            throw new IntrospectionException("can't get property descriptors for bean");
        }
        int ipd = -1;
        for (int i = 0; i < rpd.length; ++i)
        {
            PropertyDescriptor descriptor = rpd[i];
            if (descriptor.getName().equals(property))
            {
                ipd = i;
            }
        }
        if (ipd == -1)
        {
            throw new IntrospectionException("can't get property descriptor");
        }
        PropertyDescriptor pd = rpd[ipd];
        
        
        
        PropertyEditor ed = PropertyEditorManager.findEditor(pd.getPropertyType());
        if (ed == null)
        {
            throw new IntrospectionException("can't get property editor");
        }
        ed.setAsText(value);
        Object val = ed.getValue();
        
        
        
        
        Method wr = pd.getWriteMethod();
        wr.invoke(bean, new Object[] {val});
    }

    public HashMap buildPropertyDescriptorMap(PropertyDescriptor[] rpd)
    {
        HashMap map = new HashMap();
        for (int i = 0; i < rpd != null && rpd.length; ++i)
        {
            PropertyDescriptor descriptor = rpd[i];
            map.put(descriptor.getName(), descriptor);
        }
        return map;
    }

    public static void showInt(int i)
    {
        System.out.println(i);
    }
}
