/*
 * Created on Jun 17, 2005
 */
package com.surveysampling.emailpanel.counts.prototype;

import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import com.surveysampling.util.swing.LoginDialog;

/**
 * Displays a simple splash screen to the user, which
 * shows a progress message, and checks to see if the
 * user clicked the close box.
 * 
 * @author Chris Mosher
 */
public class SpotLoginDialog
{
    static SpotLoginDialog dlg;
    static Object lock = new Object();


    /**
     * @param icon
     * @return new <code>SplashScreen</code>
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public static SpotLoginDialog create(final Image icon) throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(new Runnable()
        {
            public void run()
            {
                final SpotLoginDialog d = new SpotLoginDialog(icon);
                synchronized (lock)
                {
                    SpotLoginDialog.dlg = d;
                }
            }
        });

        synchronized (lock)
        {
            return SpotLoginDialog.dlg;
        }
    }

    private final LoginDialog login;

    protected SpotLoginDialog(final Image icon) throws HeadlessException
    {
        this.login = new LoginDialog();
        final Frame frame = (Frame)this.login.getParent();
        frame.setIconImage(icon);
    }

    protected LoginDialog getLoginDialog()
    {
        return this.login;
    }

    /**
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    public void show() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        getLoginDialog().show();
                    }
                    catch (final Throwable e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            });
    }

    /**
     * @return username
     */
    public String getUsername()
    {
        return this.login.getUsername();
    }

    /**
     * @return password
     */
    public String getPassword()
    {
        return this.login.getPassword();
    }

    /**
     * @return if user closed the splash screen
     */
    public synchronized boolean wasAborted()
    {
        return !this.login.hasLoginData();
    }
}
