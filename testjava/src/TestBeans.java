import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        throws IntrospectionException, Exception, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        BeanInfo bi = Introspector.getBeanInfo(bean.getClass());
        if (bi == null)
        {
            throw new Exception("can't get info for bean");
        }
        PropertyDescriptor[] rpd = bi.getPropertyDescriptors();
        if (rpd == null)
        {
            throw new Exception("can't get property descriptors for bean");
        }
        int ipd = -1;
        for (int j = 0; j < rpd.length; ++j)
        {
            PropertyDescriptor descriptor = rpd[j];
            if (descriptor.getName().equals(property))
            {
                ipd = j;
            }
        }
        if (ipd == -1)
        {
            throw new Exception("can't get property descriptor");
        }
        PropertyDescriptor pd = rpd[ipd];
        
        
        
        PropertyEditor ed = PropertyEditorManager.findEditor(pd.getPropertyType());
        if (ed == null)
        {
            throw new Exception("can't get property editor");
        }
        ed.setAsText(value);
        Integer i = (Integer)ed.getValue();
        
        
        
        
        Method wr = pd.getWriteMethod();
        wr.invoke(bean, new Object[] {i});
    }

    public static void showInt(int i)
    {
        System.out.println(i);
    }
}
