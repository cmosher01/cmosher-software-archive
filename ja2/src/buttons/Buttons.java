/*
 * Created on Sep 18, 2007
 */
package buttons;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

public class Buttons extends WindowAdapter implements Runnable
{
	private JFrame frame;

	/**
	 * @param args
	 * @throws InvocationTargetException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, InvocationTargetException
	{
    	SwingUtilities.invokeAndWait(new Buttons());
	}

	public void run()
	{
    	try
		{
			tryRun();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	private void tryRun() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        this.frame = new JFrame();

        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(this);

        this.frame.setTitle("Button Test");

        // Create and set up the content pane.
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(320,240));
        p.add(new PowerButton(60,30));
        this.frame.setContentPane(p);

        // Set the window's size and position.
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);

        // Display the window.
        this.frame.setVisible(true);
	}

	public void windowClosing(WindowEvent e)
	{
		this.frame.dispose();
	}
}
