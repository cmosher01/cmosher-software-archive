import java.io.IOException;

public class ExceptionDuringCleanup
{
    public static void appendException(Throwable original, Throwable secondary)
    {
        appendException(original,secondary,"the following exception happened while handling the above exception:");
    }
    public static void appendException(Throwable original, Throwable secondary, String message)
    {
        /*
         * build a chain of exceptions like this:
         * original exception
         * [and its chain of exceptions]
         * Exception: message
         * secondary exception
         * [and its chain of exceptions]
         */
    
        // get to the end of the chain of original exception
        while (original.getCause() != null)
        {
            original = original.getCause();
        }

        // add a new exception, with the secondary exception
        // chain following it
        original.initCause(new Exception(message,secondary));
    }

    public static void main(String[] args) throws MyException, MyOtherException, IOException
    {
        MyException ex = null;
        MyOtherException eox = null;
        try
        {
            boolean b = true;
            // example code that throws an exception with a nested exception
            if (b)
            {
                throw new MyException("first", new MyException("first cause"));
            }
            else
            {
                throw new MyOtherException("firstother", new MyOtherException("firstother cause"));
            }
        }
        catch (MyException e)
        {
            ex = e;
            throw ex;
        }
        catch (MyOtherException e)
        {
            eox = e;
            throw eox;
        }
        // other catch blocks here...
        finally
        {
            IOException iox = null;
            try
            {
                // example clean-up code (that throws an exception with a nested exception)
                IOException ioe = new IOException("second");
                ioe.initCause(new IOException("second cause"));
                throw ioe;
            }
            catch (IOException e)
            {
                iox = e;
                if (ex != null)
                {
                    ExceptionDuringCleanup.appendException(ex,iox);
                    throw ex;
                }
                else if (eox != null)
                {
                    ExceptionDuringCleanup.appendException(eox,iox);
                    throw eox;
                }
                else
                {
                    throw iox;
                }
            }
            //other catch blocks here...
        }
    }
}
