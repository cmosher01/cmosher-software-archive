import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class TestBeans
{
    public static void main(String[] rArgs) throws Throwable
    {
        String[] rp = PropertyEditorManager.getEditorSearchPath();
        List listp = new ArrayList(Arrays.asList(rp));
        listp.add("com.surveysampling.beans.editors");
        PropertyEditorManager.setEditorSearchPath((String[])listp.toArray(new String[listp.size()]));

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
