package nu.mine.mosher.util;

public final class Closer
{
    private Closer() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Calls the given object's "close()" method, if it has one.
     * Any exceptions are ignored.
     * 
     * For example:
     * <pre>
     * OutputStream os = null;
     * try
     * {
     *     os = new FileOutputStream(new File("test.txt"));
     *     os.write(65);
     * }
     * finally
     * {
     *     UniversalCloser.close(os);
     * }
     * </pre>
     * 
     * @param obj the Object whose close() method is to be called.
     */
    public static void close(Object obj)
    {
        try
        {
            obj.getClass().getMethod("close").invoke(obj);
        }
        catch (Throwable ignore)
        {
            ignore.printStackTrace();
        }
    }
}
