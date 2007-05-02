/*
 * Created on Jun 17, 2005
 */
package com.surveysampling.emailpanel.counts.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import com.surveysampling.util.ProgressWatcher;

/**
 * Displays a simple splash screen to the user, which
 * shows a progress message, and checks to see if the
 * user clicked the close box.
 * 
 * @author Chris Mosher
 */
public class SplashScreen extends JDialog implements ProgressWatcher
{
    static SplashScreen splash;
    static Object lock = new Object();

    /**
     * @param icon
     * @return new <code>SplashScreen</code>
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public static SplashScreen create(final Image icon) throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(new Runnable()
        {
            public void run()
            {
                final SplashScreen sp = new SplashScreen(icon);
                synchronized (lock)
                {
                    SplashScreen.splash = sp;
                }
            }
        });

        synchronized (lock)
        {
            return SplashScreen.splash;
        }
    }

    protected final JLabel msg;
    private boolean aborted;

    protected SplashScreen(final Image icon) throws HeadlessException
    {
        super((Frame)null,"Spot Counts",false);

        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

        final Frame frameSplash = (Frame)getParent();
        frameSplash.setIconImage(icon);

        this.msg = new JLabel("Initializing");
        this.msg.setPreferredSize(new Dimension(190,22));

        final JPanel pane = new JPanel();
        pane.add(this.msg);

        getContentPane().add(pane,BorderLayout.CENTER);

        addWindowListener(new WindowListener()
        {
            public void windowOpened(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowClosing(WindowEvent e)
            {
                aborted();
            }
        });

        pack();
        setLocationRelativeTo(null);

        show();
    }

    /**
     * @see com.surveysampling.util.ProgressWatcher#setTotalSteps(int)
     */
    public void setTotalSteps(final int totalSteps)
    {
    }

    /**
     * @see com.surveysampling.util.ProgressWatcher#setProgress(int, java.lang.String)
     */
    public void setProgress(final int progress, final String message)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                SplashScreen.this.msg.setText(message);
            }
        });
    }

    synchronized void aborted()
    {
        this.aborted = true;
    }

    /**
     * @return if user closed the splash screen
     */
    public synchronized boolean wasAborted()
    {
        return this.aborted;
    }
}
