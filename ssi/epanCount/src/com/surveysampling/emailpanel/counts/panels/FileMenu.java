/*
 * Created on Aug 24, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import com.surveysampling.emailpanel.counts.CountsGUI;
import com.surveysampling.emailpanel.counts.Main;
import com.surveysampling.emailpanel.counts.dialogs.PrefDialog;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * The FileMenu for the application
 * @author james
 *
 */
public class FileMenu extends JMenuBar implements ActionListener
{
    private CountsGUI cg;

    public FileMenu (CountsGUI counts)
    {
        cg = counts;
        JMenu menu = new JMenu ("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.setDisplayedMnemonicIndex(0);
        
        add(menu);
        
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setMnemonic(KeyEvent.VK_N);
        newMenuItem.setDisplayedMnemonicIndex(0);
        newMenuItem.setActionCommand("New");
        newMenuItem.addActionListener(this);
        ImageIcon newIcon = Main.createImageIcon("/icon/new.gif");
        newMenuItem.setIcon(newIcon);
        menu.add(newMenuItem);
        
        menu.addSeparator();
        
        JMenuItem prefItem = new JMenuItem ("Preferences");
        prefItem.setMnemonic(KeyEvent.VK_P);
        prefItem.setDisplayedMnemonicIndex(0);
        prefItem.setActionCommand("Pref");
        prefItem.addActionListener(this);
        menu.add(prefItem);
        
        menu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem ("Exit");
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.setDisplayedMnemonicIndex(1);
        exitItem.setActionCommand("Exit");
        exitItem.addActionListener(this);
        menu.add(exitItem);
    }

    /**
     * Handles the events for the Menu bar.
     * "New" - new count request
     * "Pref" - preferences dialog to select users to see other counts
     * @see PrefDialog.java
     * "Exit" - exit the application
     */
    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand().equalsIgnoreCase("New"))
        {
            try
            {
                cg.getRightPanel().getClientInfoPanel().blankNew();
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
        else if (event.getActionCommand().equalsIgnoreCase("Pref"))
        {
            if (cg.getDirty())
            {
                JOptionPane.showMessageDialog(null,"You must SAVE or CANCEL your changes first.","Save or Cancel",JOptionPane.WARNING_MESSAGE);
                return;
            }

            PrefDialog dialog = new PrefDialog(cg);
            dialog.show();
        }
        else if (event.getActionCommand().equalsIgnoreCase("Exit"))
        {
            cg.exitApplication();
        }
        else
        	assert false;
    }
}
