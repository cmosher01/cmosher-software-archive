/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.rand.seed;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JPanel;
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
    	RandPanel panel = new RandPanel(new FlowLayout(),true);
        panel.setOpaque(true);
        panel.addNotify();
        panel.init();

        return panel;
    }
}
