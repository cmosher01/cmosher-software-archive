/*
 * Created on Jul 18, 2005
 *  
 */
package com.surveysampling.emailpanel.counts;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;

import com.surveysampling.emailpanel.counts.api.EpanCountLibrary;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeSerialNumber;
import com.surveysampling.emailpanel.counts.api.request.EpanCountRequest;
import com.surveysampling.emailpanel.counts.api.request.exception.ConcurrentModificationException;
import com.surveysampling.emailpanel.counts.data.CriterionBuilder;
import com.surveysampling.emailpanel.counts.dialogs.SplashScreen;
import com.surveysampling.emailpanel.counts.dialogs.SpotLoginDialog;
import com.surveysampling.emailpanel.counts.exception.BadInputDataException;
import com.surveysampling.emailpanel.counts.exception.ErrorSavingException;
import com.surveysampling.emailpanel.counts.listeners.ControlListener;
import com.surveysampling.emailpanel.counts.model.CountsTreeModel;
import com.surveysampling.emailpanel.counts.panels.FileMenu;
import com.surveysampling.emailpanel.counts.panels.LeftPanel;
import com.surveysampling.emailpanel.counts.panels.RightPanel;
import com.surveysampling.sql.LookupException;
import com.surveysampling.util.Flag;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * @author james
 *  
 */
public class CountsGUI
{
    private static JFrame frame;

    public static final int CHECK_EVERY_N_SECONDS = 15;

    EpanCountRequest requestBeingEdited;

    final Flag flagExit = new Flag();

    private EpanCountLibrary lib;

    private ControlListener cl = null;

    private static RightPanel rightPanel;

    private static LeftPanel leftPanel;

    private boolean dirty = false;

    private static final String GUI_ICON_PATH = "/icon/spot.gif";

    private CriterionBuilder cb;

    private CountsTreeModel ctm;

    private boolean isSaving;
    
    /**
     * Constructs the GUI to display the Count Requests
     * @throws SQLException 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public CountsGUI() throws DatalessKeyAccessCreationException, JAXBException, InterruptedException, InvocationTargetException, SQLException
    {
    	this.lib = initEpanCount();
        requestBeingEdited = lib.createEpanCountRequest();
        
		javax.swing.SwingUtilities.invokeAndWait(new Runnable()
		{
		    public void run()
		    {
		    	createAndShowGUI();
		    }
		});

    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private void createAndShowGUI()
    {
        rightPanel = new RightPanel(this);
        leftPanel = new LeftPanel();
        leftPanel.setPreferredSize(new Dimension(150,1));
    	cb = new CriterionBuilder(this);
        cl = new ControlListener(rightPanel, cb, this);
        rightPanel.setActionListener(cl);
        ctm = (CountsTreeModel) leftPanel.getTree().getModel();
        ctm.setLibrary(lib);
        ctm.setGUI(this);
        
        //Create and set up the window.
        frame = new JFrame("Spot Counts");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JScrollPane rightScroll = new JScrollPane(rightPanel);
        /*
         * Left side contains a list of requests. Right panel has
         * the information associated with each request.
         */
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel,
                rightScroll);
        sp.setOneTouchExpandable(false);
        sp.setDividerSize(5);
        frame.getContentPane().add(sp);
        frame.setJMenuBar(new FileMenu(this));   
        //Display the window.
        frame.setSize((int)(800*0.9),(int)(600*0.9));
        frame.setLocationRelativeTo(null);
        frame.show();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        Image icon = Toolkit.getDefaultToolkit().createImage(
                CountsGUI.class.getResource(GUI_ICON_PATH));
        frame.setIconImage(icon);
    }

    /**
     * Called when the application is ready to start up.
     */
    public void applicationStartupRequest()
    {
        doGUI(new Runnable()
        {
            public void run()
            {
                try
                {
                    applicationStartupRequestThread();
                }
                catch (final Throwable e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Delegate to the TreeModel, since the model holds the list
     * of requests and the model has to be populated
     * 
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    protected void applicationStartupRequestThread() throws JAXBException,
            DatalessKeyAccessCreationException
    {
        ctm.applicationStartupRequestThread();
    }

    /**
     * Executes the given task on Swing's Event Dispatch thread, and waits for
     * the task to finish. Note that if the current thread <i>is </i> the Event
     * Dispatch thread, then this method simply calls <code>task.run()</code>.
     * 
     * @param task
     */
    protected static void doGUI(final Runnable task)
    {
        try
        {
            if (SwingUtilities.isEventDispatchThread())
            {
                task.run();
            }
            else
            {
                SwingUtilities.invokeAndWait(task);
            }
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        catch (final InvocationTargetException e)
        {
            System.err.println("CAUSE: " + e.getCause());
            e.getCause().printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Puts the criteria of the selected request to the screen.
     * Delegate to the CriterionBuilder 
     */
    public void putRequestToScreen()
    {
        cb.putRequestToScreen();
    }

    /**
     * Get the inputted criteria from the screen.
     * Delegate to CriterionBuilder
     * @throws JAXBException
     * @throws BadInputDataException 
     */
    public void getRequestFromScreen() throws JAXBException, BadInputDataException
    {
        cb.getRequestFromScreen();
    }

    /**
     * @return	dirty - true, if the request has been modified
     * 					false, otherwise
     */
    public boolean getDirty()
    {
        return dirty;
    }

    /**
     * @param dirty	set to true if the request has been modified
     * 				set to false, otherwise
     */
    public void setDirty(boolean dirty)
    {
        if (this.requestBeingEdited.isFrozen())
        {
            return;
        }

        this.dirty = dirty;
    }

    /**
     * @return Returns the requestBeingEdited, the current request
     * 					that is being editted.
     */
    public EpanCountRequest getRequestBeingEdited()
    {
        return requestBeingEdited;
    }

    /**
     * @param requestBeingEdited - set this requestBeingEdited to
     * 									requestBeingEdited
     */
    public void setRequestBeingEdited(EpanCountRequest requestBeingEdited)
    {
        this.requestBeingEdited = requestBeingEdited;
    }

    /**
     * @return Returns the leftPanel.
     */
    public LeftPanel getLeftPanel()
    {
        return leftPanel;
    }

    /**
     * @return Returns the rightPanel.
     */
    public RightPanel getRightPanel()
    {
        return rightPanel;
    }

    /**
     * calls the doGUI method, so that tasks are performed in a
     * thread
     * @param runnable
     */
    public void callDoGUI(Runnable runnable)
    {
        doGUI(runnable);
    }

    public void save(final boolean buildCountChildRecords) throws ErrorSavingException
    {
        try
        {
            saveThread(buildCountChildRecords);
        }
        catch (ClassCastException e)
        {
            throw new ErrorSavingException(e);
        }
        catch (DatalessKeyAccessCreationException e)
        {
            throw new ErrorSavingException(e);
        }
        catch (InterruptedException e)
        {
            throw new ErrorSavingException(e);
        }
        catch (CloneNotSupportedException e)
        {
            throw new ErrorSavingException(e);
        }
        catch (ConcurrentModificationException e)
        {
            throw new ErrorSavingException(e);
        }
        catch (LookupException e)
        {
            throw new ErrorSavingException(e);
        }
        catch (SQLException e)
        {
            throw new ErrorSavingException(e);
        }
        catch (JAXBException e)
        {
            throw new ErrorSavingException(e);
        }
        catch (BadInputDataException e)
        {
            throw new ErrorSavingException(e);
        }
    }
    /**
     * save the current request to the database
     * @param buildCountChildRecords
     * @throws BadInputDataException 
     */
    public void saveThread(final boolean buildCountChildRecords)
            throws InterruptedException, CloneNotSupportedException,
            ClassCastException, ConcurrentModificationException,
            LookupException, SQLException, JAXBException,
            DatalessKeyAccessCreationException, BadInputDataException
    {
        //        get from screen into current
        //        dirty = false
        //        if (buildCounts)
        //            build child count records
        //        end if
        //        current = save current to database
        //        if concurrent modification
        //            de-select current in list
        //            current = clone
        //            save current to database
        //            add current to list (text, name)
        //            add current to listener
        //            select current in list
        //        else
        //            set current item in list (text)
        //            (then triggers notification of updated item from listener)
        //        end if
        //        put current to screen

        synchronized (ctm.getLockListChange())//take out a lock until change is complete
        {
        	cb.calcGeoAndWait(buildCountChildRecords);//the progress bar
    	    getRequestFromScreen();
            
            if (buildCountChildRecords)
            {
                this.requestBeingEdited.buildEpanCountChildRecords();
            }

            try
            {
                this.requestBeingEdited = 
                	this.requestBeingEdited.saveToDatabase();
                ctm.updateCurrentInList();
                this.dirty = false;
            }
            /*
             * thrown If another person is also modifying the same request
             * or if the current user selects a different request
             * while the current request is being saved
             */
            catch (final ConcurrentModificationException e)
            {	
            
                System.out.println("concurrent modification during save");
                ctm.selectCurrentInList(false);
                ctm.makeCloneOfCurrent();
                this.requestBeingEdited = 
                				this.requestBeingEdited.saveToDatabase();
                ctm.addCurrentToList();
                ctm.selectCurrentInList(true);
            }
            putRequestToScreen();
        }
    }

    /**
     * @return Returns the EpanCountLibrary
     */
    public EpanCountLibrary getLib()
    {
        return lib;
    }

    /**
     * @return Returns this frame.
     */
    public JFrame getFrame()
    {
        return frame;
    }

    /**
     * @return  true, if the current request is a new request
     * 			false, otherwise
     */
    public boolean isCurrentNew()
    {
        return (this.requestBeingEdited != null)
                && (this.requestBeingEdited.getLastChangeSerialNumber()
                        .equals(ChangeSerialNumber.getSerialNumberLowerLimit()));
    }

    /**
     * @return returns the tree model that is used
     */
    public CountsTreeModel getTreeModel()
    {
        return ctm;
    }

    /**
     *  Validate the geo criteria inputted.
     *  Delegate to CriterionBuilder
     */
    public void calcGeo(boolean forceCalcGeo)
    {
        try
        {
            cb.calcGeo(forceCalcGeo);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @return Returns the CriterionBuilder
     */
    public CriterionBuilder getCb()
    {
        return cb;
    }

    /**
     * Called when the user chooses the menu item File/Exit.
     */
    public void exitApplication()
    {
        if (destroy())
        {
            flagExit.set(true);
        }
    }

    /**
     * Prepare to shutdown
     */
    public boolean destroy()
    {
        if (this.dirty)
        {
            JOptionPane.showMessageDialog(null,
                    "You must SAVE or CANCEL your changes first.",
                    "Save or Cancel", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!cb.getRRunningCounts().isEmpty())
        {
            final int cCountsRunning = cb.getRRunningCounts().size();
            String sCounts = "" + cCountsRunning + " counts are";
            String sItThem = "them";
            if (cCountsRunning == 1)
            {
                sCounts = "1 count is";
                sItThem = "it";
            }

            final Object[] options = { "Abort counts", "Cancel" };
            final int choice = JOptionPane.showOptionDialog(null, sCounts
                    + " currently running. Are you sure you want to ABORT "
                    + sItThem + "?", "Warning", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, options, options[1]);
            if (choice != 0)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Called by the application's shutdown hook.
     * @throws CloneNotSupportedException
     * @throws ClassCastException
     * @throws ConcurrentModificationException
     * @throws LookupException
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     * @throws BadInputDataException 
     */
    public void applicationShutdownRequest() throws CloneNotSupportedException, ClassCastException, 
    												ConcurrentModificationException, LookupException, 
    												SQLException, JAXBException, DatalessKeyAccessCreationException, 
    												BadInputDataException
    {
//        stop monitor thread and join
//        if dirty
//            get from screen into current
//            save current to database
//            if concurrent modification
//                current = clone
//                save current to database
//            end if
//        end if
//        flagExit = true
        ctm.getMonitor().close();
        if (this.dirty)
        {
            getRequestFromScreen();
            try
            {
                this.requestBeingEdited = this.requestBeingEdited.saveToDatabase();
            }
            catch (final ConcurrentModificationException e)
            {
                e.printStackTrace();
                ctm.makeCloneOfCurrent();
                this.requestBeingEdited = this.requestBeingEdited.saveToDatabase();
            }
            this.dirty = false;
        }

        for (final Iterator iRunningCounts = cb.getRRunningCounts().iterator(); iRunningCounts.hasNext();)
        {
            final EpanCountRequest request = (EpanCountRequest)iRunningCounts.next();
            request.abort();
        }

        flagExit.set(true);
    }
    /**
     * @return Returns the exit flag
     */
    public Flag getFlagExit()
    {
        return flagExit;
    }
    
    private static EpanCountLibrary initEpanCount() throws InterruptedException, InvocationTargetException, SQLException
    {
        EpanCountLibrary lib = null;

        boolean ok = false;
        while (!ok)
        {
            final SpotLoginDialog login = SpotLoginDialog.create(Main.icon);
            login.show();
            if (login.wasAborted())
            {
                System.exit(1);
            }

            /*
             * Show our splash screen. The screen will show the user some progress
             * messages during the EpanCountLibrary initialization, which will take
             * at least several seconds.
             */
            final SplashScreen splash = SplashScreen.create(Main.icon);

            /*
             * Initialize the EpanCount library. This could take a while.
             */
            try
            {
                lib = new EpanCountLibrary(login.getUsername(),login.getPassword(),splash);
                ok = true;
            }
            catch (final Throwable e)
            {
                e.printStackTrace();
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        JOptionPane.showMessageDialog(null,"Error during initialization.","Spot Counts Error",JOptionPane.ERROR_MESSAGE);
                    }
                });
            }

            /*
             * If the user clicks the close box on the splash screen, then we will
             * not start up the application.
             */
            final boolean abort = splash.wasAborted();
            splash.dispose();
            if (abort)
            {
                if (lib != null)
                {
                    lib.close();
                }
                System.exit(1);
            }

        }

        return lib;
    }

    public boolean isSaving()
    {
        return this.isSaving;
    }

    public void setSaving(boolean isSaving)
    {
        this.isSaving = isSaving;
    }
    
    
}