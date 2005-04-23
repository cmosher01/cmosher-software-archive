/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.rand.seed;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import nu.mine.mosher.swingapp.ApplicationAborting;
import nu.mine.mosher.swingapp.CommandLineArgHandler;
import nu.mine.mosher.swingapp.ExceptionHandler;
import nu.mine.mosher.swingapp.SwingApplication;
import nu.mine.mosher.swingapp.SwingGUI;


/**
 * TODO
 *
 * @author Chris Mosher
 */
public class RandSeed extends SwingGUI
{
    /**
     * This class is never instantiated.
     */
    private RandSeed()
    {
        assert false : "Cannot instantiate main class.";
    }

    /**
     * @param args
     * @throws ApplicationAborting
     */
    public static void main(String[] args) throws ApplicationAborting
    {
        ExceptionHandler eh = new ExceptionHandler();
        CommandLineArgHandler ch = new CommandLineArgHandler(args);
        SwingGUI gui = new RandSeed();

        SwingApplication app = new SwingApplication(eh,ch,gui);

        app.run();
    }

    protected JPanel createContentPane()
    {
        JPanel panel = new RandPanel(new BorderLayout(),true);
        panel.setOpaque(true);
        panel.addNotify();

        return panel;
    }
}
