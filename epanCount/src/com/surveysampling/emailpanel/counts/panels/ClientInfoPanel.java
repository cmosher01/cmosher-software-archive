/*
 * Created on Jul 18, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.xml.bind.JAXBException;

import com.surveysampling.emailpanel.counts.CountsGUI;
import com.surveysampling.emailpanel.counts.Main;
import com.surveysampling.emailpanel.counts.exception.BadInputDataException;
import com.surveysampling.emailpanel.counts.model.CountsTreeModel;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * @author james
 *
 */
public class ClientInfoPanel extends JPanel implements ActionListener, KeyListener
{
    JTextField clientField, topicField;

    CountsGUI cg;
    // just enough String to keep sizes of buttons and the label
    // consistent. An empty string "" that changes to a label
    // will force a resize.
    JLabel accountExecLabel = new JLabel(" ");

    public ClientInfoPanel(CountsGUI countsGUI)
    {
        cg = countsGUI;
        setLayout(new BorderLayout());
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        
        JPanel clientPanel = new JPanel();
        clientPanel.add(new JLabel("client:"));
        clientField = new JTextField();
        clientField.setColumns(30);
        clientField.addKeyListener(this);
        clientPanel.add(clientField);
        
        JPanel topicPanel = new JPanel();
        topicPanel.add(new JLabel("topic: "));
        topicField = new JTextField();
        topicField.setColumns(30);
        topicField.addKeyListener(this);
        topicPanel.add(topicField);
        
        leftPanel.add(clientPanel);
        leftPanel.add(topicPanel);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        ImageIcon newIcon = Main.createImageIcon("/icon/new.gif");
        JButton newButton = new JButton("New", newIcon);
        newButton.addActionListener(this);
        newButton.setActionCommand("New");
        JPanel dp = new JPanel(new BorderLayout());
        dp.add(newButton, BorderLayout.CENTER);
        rightPanel.add(dp);
        
        ImageIcon copyIcon = Main.createImageIcon("/icon/copy.gif");
        JButton copyButton = new JButton("Copy as New", copyIcon);
        copyButton.addActionListener(this);
        copyButton.setActionCommand("Copy");
        JPanel cp = new JPanel(new BorderLayout());
        cp.add(copyButton, BorderLayout.CENTER);
        rightPanel.add(cp);
        
        JPanel aePanel = new JPanel();
        accountExecLabel.setVerticalAlignment(SwingConstants.TOP);
        accountExecLabel.setVerticalTextPosition(SwingConstants.TOP);
        aePanel.add(accountExecLabel);
        rightPanel.add(aePanel);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        setPreferredSize(new Dimension(500,75));
    }
    
    
    /**
     * @return Returns text of the clientField.
     */
    public String getClientText()
    {
        return clientField.getText();
    }
    /**
     * @param text The text to set for the clientField.
     */
    public void setClientText(String text)
    {
        clientField.setText(text);
    }
    /**
     * @return Returns the text of the topicField.
     */
    public String getTopicText()
    {
        return topicField.getText();
    }
    /**
     * @param text The text to set for the TopicField
     */
    public void setTopicText(String text)
    {
        topicField.setText(text);
    }

    /**
     * @param enable
     */
    public void setFieldsEnable(boolean enable)
    {
        clientField.setEnabled(enable);
        topicField.setEnabled(enable);
    }


    /**
     * Handles the actions of this panel.
     * - "New" - a new request
     * - "Copy To New" - copy the current request to a new request
     */
    public void actionPerformed(ActionEvent arg0)
    {
        try
        {
            if (arg0.getActionCommand().equals("New"))
                blankNew();
            else if (arg0.getActionCommand().equalsIgnoreCase("Copy"))
                copyToNew();
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        catch (DatalessKeyAccessCreationException e)
        {
            e.printStackTrace();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        } catch (BadInputDataException e) 
        {
			e.printStackTrace();
		}
    }
    
    /**
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public void blankNew() throws JAXBException, DatalessKeyAccessCreationException
    {
//        if dirty
//            "must save or cancel first"
//        else
//            if current not null and current has never been saved to database
//                remove current from list
//                remove current from listener
//            else
//                de-select current in list
//            end if
//            current = create new
//            put current to screen
//            add current to list (text, name)
//            add current to listener
//            select current in list
//        end if
        synchronized (cg.getTreeModel().getLockListChange())
        {
            if (cg.getDirty())
            {
                JOptionPane.showMessageDialog(null,"You must SAVE or CANCEL your changes first.","Save or Cancel",JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            CountsTreeModel ctm = cg.getTreeModel();
            if (cg.isCurrentNew())
            {
                ctm.removeCurrentFromList();
            }
            else
            {
                ctm.selectCurrentInList(false);
            }

            cg.setRequestBeingEdited(cg.getLib().createEpanCountRequest());
            cg.putRequestToScreen();
            ctm.addCurrentToList();
            ctm.selectCurrentInList(true);
        }
    }
    
    /**
     * @throws CloneNotSupportedException
     * @throws JAXBException
     * @throws BadInputDataException 
     */
    public void copyToNew() throws CloneNotSupportedException, JAXBException, BadInputDataException
    {
//        if dirty
//            "must save or cancel first"
//        else
//            if current has never been saved to database
//                remove current from list
//                remove current from listener
//            else
//                de-select current in list
//            end if
//            get from screen into current
//            current = clone
//            put current to screen
//            add current to list (text, name)
//            add current to listener
//            select current in list
//            dirty = true
//        end if
        synchronized (cg.getTreeModel().getLockListChange())
        {
            if (cg.getDirty())
            {
                JOptionPane.showMessageDialog(null,"You must SAVE or CANCEL your changes first.","Save or Cancel",JOptionPane.WARNING_MESSAGE);
                return;
            }

            CountsTreeModel ctm = cg.getTreeModel();
            if (cg.isCurrentNew())
            {
                ctm.removeCurrentFromList();
            }
            else
            {
                ctm.selectCurrentInList(false);
            }

            cg.getRequestFromScreen();
            ctm.makeCloneOfCurrent();
            cg.putRequestToScreen();
            ctm.addCurrentToList();
            ctm.selectCurrentInList(true);
            cg.setDirty(true);
        }
    }
    /**
     * @return Returns the AccountExecutive label
     */
    public JLabel getAeLabel()
    {
        return accountExecLabel;
    }

    /**
     * Only added as implementation of KeyListner
     */
	public void keyPressed(KeyEvent e)
	{
		//do nothing
	}

	/**
	 * Only added as implementation of KeyListner
	 */
	public void keyReleased(KeyEvent e)
	{
		//do nothing
	}


	/**
	 * If a key has been typed, then set the request 
	 * to dirty.
	 */
	public void keyTyped(KeyEvent e)
	{
		cg.setDirty(true);
	}
}
