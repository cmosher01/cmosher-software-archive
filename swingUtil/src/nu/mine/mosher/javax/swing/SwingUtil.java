/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.javax.swing;

import javax.swing.UIManager;

/**
 * Contains static methods relating to Java's Swing API.
 * 
 * @author Chris Mosher
 */
public final class SwingUtil
{
    private SwingUtil()
    {
        assert false : "can't instantiate";
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
        catch (final Throwable ignoreAnyExceptions)
        {
        	ignoreAnyExceptions.printStackTrace();
        }
    }
}
