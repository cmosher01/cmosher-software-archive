import java.lang.reflect.Method;

public class UniversalCloser
{
    public static void close(Object obj)
    {
        try
        {
            Method methodClose = obj.getClass().getMethod("close",null);
            methodClose.invoke(obj,null);
        }
        catch (Throwable ignore)
        {
        }
    }
}
