public class UniversalCloser
{
    public static void close(Object obj)
    {
        try
        {
            obj.getClass().getMethod("close",null).invoke(obj,null);
        }
        catch (Throwable ignore)
        {
        }
    }
}
