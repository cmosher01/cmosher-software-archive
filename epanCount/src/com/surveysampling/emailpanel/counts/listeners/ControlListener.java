/*
 * Created on Jul 26, 2005
 *
 */
package com.surveysampling.emailpanel.counts.listeners;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import com.surveysampling.emailpanel.counts.CountsGUI;
import com.surveysampling.emailpanel.counts.api.request.EpanCount;
import com.surveysampling.emailpanel.counts.api.request.EpanCountDoneListener;
import com.surveysampling.emailpanel.counts.api.request.EpanCountRequest;
import com.surveysampling.emailpanel.counts.api.request.exception.ConcurrentModificationException;
import com.surveysampling.emailpanel.counts.data.CriterionBuilder;
import com.surveysampling.emailpanel.counts.exception.BadInputDataException;
import com.surveysampling.emailpanel.counts.exception.ErrorSavingException;
import com.surveysampling.emailpanel.counts.model.CountsTreeModel;
import com.surveysampling.emailpanel.counts.panels.RightPanel;
import com.surveysampling.sql.LookupException;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Handles much of the action of the Counts application
 * 
 * @author james
 */
public class ControlListener implements ActionListener
{
    RightPanel rp;
    CriterionBuilder cb;
    CountsGUI cg;
    
    /**
     * Constructor
     * @param rightPanel
     * @param criterion
     * @param countsGUI
     */
    public ControlListener(RightPanel rightPanel, CriterionBuilder criterion, CountsGUI countsGUI)
    {
        rp = rightPanel;
        cb = criterion;
        cg = countsGUI;
    }
    /**
     * Handles the action or clicking "Run", "Save", "Abort", "Cancel", "Delete"
     * 	"Full Report", "Tab Delim", etc
     */
    public void actionPerformed(ActionEvent event)
    {
        try
        {
			handleAction(event);
		}
        catch (final ErrorSavingException ese)
        {
            cg.setSaving(false);
            ese.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error while saving: "+ese.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error occurred: "+e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
    
    /**
     * Handles the event
     * @param event
     * @throws ErrorSavingException 
     * @throws JAXBException 
     * @throws SQLException 
     * @throws DatalessKeyAccessCreationException 
     * @throws BadInputDataException 
     */
	private void handleAction(ActionEvent event) throws ErrorSavingException, DatalessKeyAccessCreationException, SQLException, JAXBException, BadInputDataException
	{
		if (event.getActionCommand().equalsIgnoreCase("Run"))
        {
            runCount();    
        }
        else if (event.getActionCommand().equalsIgnoreCase("Abort"))
        {
            abort();
        }
        else if (event.getActionCommand().equalsIgnoreCase("Save"))
        {
            save();
        }
        else if (event.getActionCommand().equalsIgnoreCase("Cancel"))
        {
        	cancel();
        }
        else if (event.getActionCommand().equalsIgnoreCase("Delete"))
        {
        	delete();
        }
        else if (event.getActionCommand().equalsIgnoreCase("Full"))
        {
        	reportToClipboard();
        }
        else if (event.getActionCommand().equalsIgnoreCase("Tab"))
        {
        	csvToClipboard();
	            
        }
        else
            assert false;
	}
    
    /**
     * Saves the request, if the request is dirty.
     * @throws ErrorSavingException 
     * 
     */
    private void save() throws ErrorSavingException
    {
    	if (!cg.getDirty())
        {
            return;
        }
        cg.setSaving(true);
        cg.save(false);
        cg.setSaving(false);
    }
    
    /**
     * Cancels any changes made to this request
     * 
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     * 
     */
    private void cancel() throws SQLException, JAXBException, DatalessKeyAccessCreationException
    {
//        if dirty, ask user
//        if current has never been saved to database
//            remove current from list
//            remove current from listener
//            current = create new
//            put current to screen
//            add current to list (text, name)
//            add current to listener
//            select current in list
//        else
//            current = read from database
//            put current to screen
//            set current in list (text)
//        end if
        synchronized (cg.getTreeModel().getLockListChange())
        {
            if (cg.getDirty())
            {
                final Object[] options = {"Discard changes","Keep changes"};
                final int choice = JOptionPane.showOptionDialog(null,"Are you sure you want to DISCARD your changes?",
                    "Warning",JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,null,options,options[1]);
                if (choice != 0)
                {
                    return;
                }
            }

            cg.setDirty(false);
            CountsTreeModel ctm = cg.getTreeModel();
            if (cg.isCurrentNew())
            {
                ctm.removeCurrentFromList();
                cg.setRequestBeingEdited(cg.getLib().createEpanCountRequest());
                cg.putRequestToScreen();
                ctm.addCurrentToList();
                ctm.selectCurrentInList(true);
            }
            else
            {
                cg.setRequestBeingEdited(cg.getLib().readEpanCountRequest(cg.getRequestBeingEdited().getPK()));
                ctm.updateCurrentInList();
                cg.putRequestToScreen();
            }
        }
    }

    /**
     * Called when the user presses the Delete button.
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    private void delete() throws SQLException, JAXBException, DatalessKeyAccessCreationException
    {
//        if dirty, ask user
//        if current has never been saved to database
//            remove current from list
//            remove current from listener
//        else
//            delete from database
//            (triggers notification of deleted item from listener; see above)
//            de-select current in list
//        end if
//        current = create new
//        put current to screen
//        add current to list (text, name)
//        add current to listener
//        select current in list
//        dirty = false

        synchronized (cg.getTreeModel().getLockListChange())
        {
            final Object[] options = {"Delete request","Keep request"};
            final int choice = JOptionPane.showOptionDialog(null,"Are you sure you want to PERMANENTLY DELETE this request?",
                "Warning",JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,null,options,options[1]);
            if (choice != 0)
            {
                return;
            }
            CountsTreeModel ctm = cg.getTreeModel();
            if (cg.isCurrentNew())
            {
                ctm.removeCurrentFromList();
            }
            else
            {
                cg.getRequestBeingEdited().delete();
                ctm.getMonitor().forceCheckNow();
                ctm.selectCurrentInList(false);
            }
            cg.setRequestBeingEdited(cg.getLib().createEpanCountRequest());
            cg.putRequestToScreen();
            ctm.addCurrentToList();
            ctm.selectCurrentInList(true);
            cg.getLeftPanel().getTree().repaint();
            cg.setDirty(false);
        }
    }
    /**
     * Submits the count queries to the database and runs the query.
     * @throws ErrorSavingException 
     */
    private void runCount() throws ErrorSavingException
    {
        final EpanCountRequest requestToRun = cg.getRequestBeingEdited();

        synchronized (ControlListener.class)
        {
            if (requestToRun.isFrozen())
            {
                return;
            }
            requestToRun.setFrozen(true);
        }
        
        try
        {
			cg.setSaving(true); 
			cg.save(true);
			cg.setSaving(false);
        }
        catch (final ErrorSavingException e)
        {
            requestToRun.setFrozen(false);
            throw e;
        }

        cg.getCb().getRRunningCounts().add(requestToRun);

        final Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    runCountThread(requestToRun);
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * Runs the request and forces a check on the Monitor. The
     * check will make any necessary changes to the JTree.
     * @see CountsTreeModel.itemChanged(...)
     * @param requestToRun
     * @throws SQLException
     */
    private void runCountThread(final EpanCountRequest requestToRun) throws SQLException
    {
        final CountsTreeModel ctm = cg.getTreeModel();
        requestToRun.run(new EpanCountDoneListener()
        {
            public void done(final EpanCount epanCount)
            {
                ctm.getMonitor().forceCheckNow();
            }
            public void allDone(final EpanCountRequest epanCountRequest)
            {
                cg.getCb().getRRunningCounts().remove(epanCountRequest);
            }
        });
    }
    
    /**
     * Called when the user presses the Abort button.
     */
    private void abort()
    {
        int iRunning = cg.getCb().getRRunningCounts().indexOf(cg.getRequestBeingEdited());
        if (iRunning < 0)
        {
            System.err.println("Cannot find request to abort.");
            return;
        }

        cg.getRightPanel().getRunPanel().setAbortEnable(false);
        final EpanCountRequest requestRunning = (EpanCountRequest)cg.getCb().getRRunningCounts().get(iRunning);
        requestRunning.abort();
    }
    
    /**
     * Called when the user presses the "Full report" button
     * 
     * @throws JAXBException
     * @throws BadInputDataException 
     */
    private void reportToClipboard() throws JAXBException, BadInputDataException
    {
        if (cg.getDirty())
        {
            cg.getRequestFromScreen();
        }
        final StringBuffer sb = new StringBuffer(8*1024);
        appendReport(sb);
        copyToClipboard(sb);
    }

    /**
     * Called when the user presses the "Tab delim." button
     * 
     * @throws JAXBException
     * @throws BadInputDataException 
     */
    private void csvToClipboard() throws JAXBException, BadInputDataException
    {
        if (cg.getDirty())
        {
            cg.getRequestFromScreen();
        }
        final StringBuffer sb = new StringBuffer(2*1024);
        appendCounts(sb);
        copyToClipboard(sb);
    }

    /**
     * Append to the report the information of this request
     * @param sb
     */
    private void appendReport(final StringBuffer sb)
    {
        cg.getLib().getCountReportBuilder().appendReport(cg.getRequestBeingEdited(),sb);
    }

    /**
     * Append the count request items
     * @param sb
     */
    private void appendCounts(final StringBuffer sb)
    {
        for (final Iterator iCount = cg.getRequestBeingEdited().iterator(); iCount.hasNext();)
        {
            final EpanCount count = (EpanCount)iCount.next();

            final String nameOrig = count.getName().trim();
            final String nameDoubleUpQuotes = nameOrig.replaceAll("\"","\"\"");

            // format: "nameWithAnyQuotesDoubled"\tcount\n
            sb.append("\"");
            sb.append(nameDoubleUpQuotes);
            sb.append("\"\t");
            if (count.completedSuccessfully())
            {
                sb.append(Long.toString(count.getCount()));
            }
            sb.append("\n");
        }
    }

    /**
     * Copy the report to a clipboard.
     * 
     * @param source
     */
    private void copyToClipboard(final StringBuffer source)
    {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final StringSelection csv = new StringSelection(source.toString());
        clipboard.setContents(csv,csv);
    }
    
}
