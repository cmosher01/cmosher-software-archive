import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

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
        System.out.println(ed.getValue().getClass().getName());
    }
}
