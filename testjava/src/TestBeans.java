import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;

public class TestBeans
{
    public static void main(String[] rArgs) throws Throwable
    {
        PropertyEditor ed = PropertyEditorManager.findEditor(Integer.TYPE);
        if (ed == null)
        {
            throw new Exception("can't get property editor for integer");
        }
        ed.setAsText("34");
        Integer i = (Integer)ed.getValue();
        showInt(i.intValue());

        BeanInfo bi = Introspector.getBeanInfo(SomeBean.class);
        if (bi == null)
        {
            throw new Exception("can't get info for SomeBean");
        }
        PropertyDescriptor[] rpd = bi.getPropertyDescriptors();
        if (rpd == null)
        {
            throw new Exception("can't get property descriptors for SomeBean");
        }
        int ipd = -1;
        for (int j = 0; j < rpd.length; ++j)
        {
            PropertyDescriptor descriptor = rpd[j];
            if (descriptor.getName().equals("AInt"))
            {
                ipd = j;
            }
        }
        if (ipd == -1)
        {
            throw new Exception("can't get AInt property descriptors for SomeBean");
        }
        Method wr = rpd[ipd].getWriteMethod();
        SomeBean some = new SomeBean();
        wr.invoke(bi, new Object[] {i});
        showInt(some.getAInt());
    }

    public static void showInt(int i)
    {
        System.out.println(i);
    }
}
