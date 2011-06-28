/*
 * Created on Jul 20, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.surveysampling.emailpanel.counts.CountsGUI;
import com.surveysampling.emailpanel.counts.Main;

/**
 * @author james
 *
 */
public class RunPanel extends JPanel implements ActionListener
{
    ImageIcon runIcon = Main.createImageIcon("/icon/search.gif");
    ImageIcon abortIcon = Main.createImageIcon("/icon/abort.gif"); 
    JButton runButton = new JButton("Run", runIcon);
    JButton abortButton = new JButton("Abort", abortIcon);
    JCheckBox geoBox = new JCheckBox("Geography");
    JCheckBox genderBox = new JCheckBox("Male/Female");

    CountsGUI cg;
    /**
     * Constructor
     * @param countsGUI
     */
    public RunPanel(CountsGUI countsGUI)
    {
        super();
        cg = countsGUI;
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel breakProp = new JPanel();
        breakProp.setLayout(new FlowLayout());
        breakProp.add(new JLabel("break out by: "));
        geoBox.addActionListener(this);
        genderBox.addActionListener(this);
        breakProp.add(geoBox);
        breakProp.add(genderBox);
        JPanel commandButtons = new JPanel();
        commandButtons.setLayout(new FlowLayout());
        
        commandButtons.add(runButton);
        commandButtons.add(abortButton);
        
        panel.add(breakProp,BorderLayout.LINE_START);
        panel.add(commandButtons, BorderLayout.LINE_END);
        setPreferredSize(new Dimension(1,30));
        add(panel, BorderLayout.CENTER);
        
    }

    /**
     * 
     * @return	true, if geoBox is checked
     * 			false, otherwise
     */
    public boolean isGeographySelected()
    {
        return geoBox.isSelected();
    }
    
    /**
     * 
     * @param selected	set the geoBox to selected
     */
    public void setGeography(boolean selected)
    {
        geoBox.setSelected(selected);
    }
    
    /**
     * 
     * @return	true, if gender is selected
     * 			false, otherwise
     */
    public boolean isGenderSelected()
    {
       return genderBox.isSelected();
    }
    
    /**
     * Set the genderBox to selected
     * @param selected
     */
    public void setGender(boolean selected)
    {
        genderBox.setSelected(selected);
    }
    
    /**
     * Enable or disable the 2 checkboxes
     * @param enable
     */
    public void setFieldsEnable(boolean enable)
    {
        geoBox.setEnabled(enable);
        genderBox.setEnabled(enable);
    }
    /**
     * @param listener	set the ActionListener to listener
     */
    public void setActionListener(ActionListener listener)
    {
        runButton.setActionCommand("Run");
        runButton.addActionListener(listener);
        
        abortButton.setActionCommand("Abort");
        abortButton.addActionListener(listener);
        
    }

    /**
     * Enable or disable the runButton
     * @param enable
     */
    public void setRunEnable(boolean enable)
    {
        runButton.setEnabled(enable);
    }
    
    /**
     * Enable or disable the abortButton
     * @param enable
     */
    public void setAbortEnable(boolean enable)
    {
        abortButton.setEnabled(enable);
    }


    /**
     * If the geoBox or the genderBox's are 
     * checked/unchecked, then set this dirty.
     */
    public void actionPerformed(ActionEvent arg0)
    {
        cg.setDirty(true);
    }
    
}
