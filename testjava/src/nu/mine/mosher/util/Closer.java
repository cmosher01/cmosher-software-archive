public class UniversalCloser
{
    /**
     * Calls the given object's "close()" method, if it has one.
     * Any exceptions are ignored.
     * 
     * @param obj the Object whose close() method is to be called.
     */
    public static void close(Object obj)
    {
        if (obj == null)
        {
            return;
        }

        try
        {
            obj.getClass().getMethod("close",null).invoke(obj,null);
        }
        catch (Throwable ignore)
        {
        }
    }
}
