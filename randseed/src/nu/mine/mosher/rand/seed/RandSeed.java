/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.rand.seed;

import java.awt.FlowLayout;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer.Info;
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
	private RandomSeedKeyListener rand = new RandomSeedKeyListener();

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
//        ExceptionHandler eh = new ExceptionHandler();
//        CommandLineArgHandler ch = new CommandLineArgHandler(args);
//        SwingGUI gui = new RandSeed();
//
//        SwingApplication app = new SwingApplication(eh,ch,gui);
//
//        app.run();
    	Info[] rInfo = AudioSystem.getMixerInfo();
    	for (int i = 0; i < rInfo.length; i++)
		{
			Info info = rInfo[i];
			System.out.println(info.toString());
		}
    }

    protected JPanel createContentPane()
    {
    	RandPanel panel = new RandPanel(new FlowLayout(),true);
        panel.setOpaque(true);
        panel.addNotify();
        panel.init(this.rand);

        return panel;
    }
}
