/*
 * Created on Aug 24, 2005
 *
 */
package com.surveysampling.emailpanel.counts.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.bind.JAXBException;

import com.surveysampling.emailpanel.counts.CountsGUI;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeMonitor;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableSet;
import com.surveysampling.emailpanel.counts.api.list.users.User;
import com.surveysampling.emailpanel.counts.api.list.users.UserAccess;
import com.surveysampling.emailpanel.counts.data.OldFolder;
import com.surveysampling.emailpanel.counts.data.RecentFolder;
import com.surveysampling.emailpanel.counts.data.TodayFolder;
import com.surveysampling.emailpanel.counts.model.CountsTreeModel;
import com.surveysampling.emailpanel.counts.renderer.CountsListCellRenderer;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * @author james
 *
 */
public class PrefDialog extends JDialog implements ActionListener
{
    CountsGUI cg;
    final JList listUsers = new JList();
    
    /**
     * Construct the Preferences Dialog
     * @param counts
     */
    public PrefDialog(CountsGUI counts)
    {
        super(counts.getFrame(), "Preferences Dialog", true);
        setSize(new Dimension(400,300));
        cg = counts;
        int xCenter = cg.getFrame().getX() + cg.getFrame().getWidth()/2;
        int yCenter = cg.getFrame().getY() + cg.getFrame().getHeight()/2;
        int xPoint = xCenter - 200;
        int yPoint = yCenter - 150;
        setLocation(xPoint, yPoint);//set the location at the center of the frame
        
        listUsers.setCellRenderer(new CountsListCellRenderer());
        try
        {
            preferences();//populate the list
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        getContentPane().setLayout(new BorderLayout());
        JLabel label = new JLabel("Whose counts do you want to see (in addition to your own)?");
        getContentPane().add(label, BorderLayout.PAGE_START);
        
        JPanel panel = new JPanel();
        panel.add(listUsers);
        
        JScrollPane scroll = new JScrollPane(listUsers);
        getContentPane().add(scroll, BorderLayout.CENTER);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setActionCommand("OK");
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setActionCommand("Cancel");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
    }
    /**
     * Called when the user chooses the menu item File/Preferences.
     * @throws IOException
     * @throws SQLException
     */
    public void preferences() throws IOException, SQLException
    {

        final UserAccess userAccess = cg.getLib().getUserAccess();
        final List rUser = new ArrayList();
        userAccess.getUsers(rUser);

        Vector vector = new Vector();
        ArrayList selectItems = new ArrayList();
        for (final Iterator iUser = rUser.iterator(); iUser.hasNext();)
        {
            final User user = (User)iUser.next();

            final JLabel widgetUser = new JLabel();

            final StringBuffer text = new StringBuffer(64);
            text.append(user.getNameLast());
            text.append(", ");
            text.append(user.getNameFirst());
            text.append(" (");
            text.append(user.getDept());
            text.append(")");
            
            widgetUser.setText(text.toString());
            widgetUser.putClientProperty("userObj", user);
            vector.add(widgetUser);
            if (user.isSeen())
            {
                int i = vector.indexOf(widgetUser);
                selectItems.add(new Integer(i));
            }
        }
        listUsers.setListData(vector);
        int [] indices = new int[selectItems.size()];
        for (int i = 0; i < selectItems.size(); i++)
        {
            indices[i] = ((Integer)selectItems.get(i)).intValue();
        }
        
        listUsers.setSelectedIndices(indices);
    }

    /**
     * Called when the users presses the OK button on the Preferences dialog.
     * @param dlg
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public void prefsOK() throws SQLException, JAXBException, DatalessKeyAccessCreationException
    {
        cg.getTreeModel().getMonitor().close();

        final List rUser = new ArrayList(listUsers.getModel().getSize());
        for (int iItem = 0; iItem < listUsers.getModel().getSize(); ++iItem)
        {
            final JLabel widgetUser = (JLabel) listUsers.getModel().getElementAt(iItem);
            final User user = (User)widgetUser.getClientProperty("userObj");
            user.setSeen(isValueSelected(widgetUser, listUsers.getSelectedValues()));
            rUser.add(user);
        }
        this.dispose();

        final UserAccess userAccess = cg.getLib().getUserAccess();
        userAccess.setSeenUsers(rUser);

        synchronized (cg.getTreeModel().getLockListChange())
        {
            cg.getLeftPanel().getOldNode().removeAllChildren();
            cg.getLeftPanel().getTodayNode().removeAllChildren();
            cg.getLeftPanel().getRecentNode().removeAllChildren();
            
            cg.getLeftPanel().getOldNode().setUserObject(new OldFolder());
            cg.getLeftPanel().getTodayNode().setUserObject(new TodayFolder());
            cg.getLeftPanel().getRecentNode().setUserObject(new RecentFolder());

            CountsTreeModel ctm = cg.getTreeModel();
            ctm.reload();
            final MonitorableSet monset = cg.getTreeModel().getMonitor().getMonitorableSet();
            ctm.setMonitor(new ChangeMonitor(monset,CountsGUI.CHECK_EVERY_N_SECONDS));
            ctm.addChangeListenerToMonitor();
            ctm.getMonitor().forceCheckNow();
            cg.setRequestBeingEdited(cg.getLib().createEpanCountRequest());
            cg.putRequestToScreen();
            ctm.addCurrentToList();
            cg.getLeftPanel().getTree().expandRow(0);
            ctm.selectCurrentInList(true);
            cg.setDirty(false);
        }
    }

    /**
     * Checks if widgetUser is one of the selected values
     * 
     * @param widgetUser
     * @param selectedValues
     * @return	true, if widgetUser is selected
     * 			false, otherwise
     */
    private boolean isValueSelected(JLabel widgetUser, Object[] selectedValues)
    {
        for (int i =0; i < selectedValues.length; i++)
            if (widgetUser.equals(selectedValues[i]))
                return true;
        return false;
    }
    /**
     * Called when the users presses the Cancel button on the Preferences dialog.
     * @param dlg
     */
    public void prefsCancel()
    {
        this.dispose();
    }
    
    /**
     * Handles the action of clicking "Cancel" or "OK"
     */
    public void actionPerformed(ActionEvent arg0)
    {
        if (arg0.getActionCommand().equalsIgnoreCase("Cancel"))
            prefsCancel();
        else
            try
            {
                prefsOK();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            catch (JAXBException e)
            {
                e.printStackTrace();
            }
            catch (DatalessKeyAccessCreationException e)
            {
                e.printStackTrace();
            }
    }
}
