public class TestExceptions
{
    public static class MyThrowable extends Throwable
    {
    }
    public static void main(String[] args) throws Throwable
    {
        try
        {
            throw new MyThrowable();
        }
        catch (RuntimeException e)
        {
            System.err.println("catch RuntimeException");
            throw e;
        }
        catch (Error e)
        {
            System.err.println("catch Error");
            throw e;
        }
        catch (Exception e)
        {
            System.err.println("catch Exception");
            throw e;
        }
        catch (Throwable e)
        {
            System.err.println("catch Throwable");
            throw e;
        }
        finally
        {
            System.err.println("finally");
        }
    }
}
