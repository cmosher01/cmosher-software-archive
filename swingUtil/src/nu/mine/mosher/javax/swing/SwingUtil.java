/*
 * Created on Jul 15, 2004
 */
package nu.mine.mosher.javax.swing;

/**
 * Contains static methods relating to Java's Swing API.
 * 
 * @author Chris Mosher
 */
public final class SwingUtil
{
    private SwingUtil() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets Swing's current look and feel to the native
     * operating system's look and feel.
     */
    public static void useOSLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Throwable ignoreAnyExceptions)
        {
        }
    }
}
