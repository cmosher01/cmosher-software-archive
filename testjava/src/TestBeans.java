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
        String[] rp = PropertyEditorManager.getEditorSearchPath();
        for (int i = 0; i < rp.length; ++i)
        {
            String string = rp[i];
            System.out.println(string);
        }
        PropertyEditorManager.setEditorSearchPath(new String[] {"com.surveysampling.beans.editors"});

        SomeBean some = new SomeBean();
        String prop = "AInt";
        String val = "34";







        BeanInfo bi = Introspector.getBeanInfo(SomeBean.class);
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
            if (descriptor.getName().equals(prop))
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
        ed.setAsText(val);
        Integer i = (Integer)ed.getValue();




        Method wr = pd.getWriteMethod();
        wr.invoke(some, new Object[] {i});



        showInt(some.getAInt());
    }

    public static void showInt(int i)
    {
        System.out.println(i);
    }
}
