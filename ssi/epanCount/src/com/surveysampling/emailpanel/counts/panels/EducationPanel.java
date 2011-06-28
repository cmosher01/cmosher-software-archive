/*
 * Created on Jul 18, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.surveysampling.emailpanel.counts.CountsGUI;

/**
 * @author james
 *
 */
public class EducationPanel extends JPanel implements ActionListener
{

    JCheckBox someHSBox = new JCheckBox("some HS");
    JCheckBox hsGradBox = new JCheckBox("HS grad");
    JCheckBox someCollegeBox = new JCheckBox("some College");
    JCheckBox collegeDegBox = new JCheckBox("College degree");
    JCheckBox someGradBox = new JCheckBox("some grad");
    JCheckBox mastersBox = new JCheckBox("Masters");
    JCheckBox professionalBox = new JCheckBox ("Dr/Law/Pro");
    private CountsGUI cg;

    public EducationPanel(CountsGUI cg)
    {
        super();
        this.cg = cg;
        setLayout(new FlowLayout(FlowLayout.LEFT, 1, 0));
        
        add(new JLabel(" edu:"));
        add(someHSBox);
        someHSBox.addActionListener(this);
        add(hsGradBox);
        hsGradBox.addActionListener(this);
        add(someCollegeBox);
        someCollegeBox.addActionListener(this);
        add(collegeDegBox);
        collegeDegBox.addActionListener(this);
        add(new JLabel(""));
        add(someGradBox);
        someGradBox.addActionListener(this);
        add(mastersBox);
        mastersBox.addActionListener(this);
        add(professionalBox);
        professionalBox.addActionListener(this);
    }
    
    /**
     * @return Returns if the collegeDegree box is selected
     */
    public boolean isColDeg()
    {
        return collegeDegBox.isSelected();
    }
    /**
     * @param select select/deselect the collegeDegree Box
     */
    public void setColDeg(boolean select)
    {
        collegeDegBox.setSelected(select);
    }
    /**
     * @return 	true, if hsGrad Box is selected
     * 			otherwise, false
     */
    public boolean isHsGrad()
    {
        return hsGradBox.isSelected();
    }
    /**
     * @param select select/deselect the hsGrad Box
     */
    public void setHsGrad(boolean select)
    {
        hsGradBox.setSelected(select);
    }
    /**
     * @return  true, if the masters box is selected
     * 			otherwise, false
     */
    public boolean isMasters()
    {
        return mastersBox.isSelected();
    }
    /**
     * @param select select/deselect the mastersBox
     */
    public void setMasters(boolean select)
    {
        mastersBox.setSelected(select);
    }
    /**
     * @return  true, if the professional box is selected
     * 			otherwise, false
     */
    public boolean isPro()
    {
        return professionalBox.isSelected();
    }
    /**
     * @param select select/deselect the professionalBox
     */
    public void setPro(boolean select)
    {
        professionalBox.setSelected(select);
    }
    /** 
     * @return  true, if the someCollege box is selected
     * 			otherwise, false
     */
    public boolean isSmCollege()
    {
        return someCollegeBox.isSelected();
    }
    /**
     * @param select select/deselect the someCollege Box
     */
    public void setSmCollege(boolean select)
    {
        someCollegeBox.setSelected(select);
    }
    /**
     * @return  true, if the someGrad box is selected
     * 			otherwise, false
     */
    public boolean isSmGrad()
    {
        return someGradBox.isSelected();
    }
    /**
     * @param select select/deselect the someGradBox
     */
    public void setSmGrad(boolean select)
    {
        someGradBox.setSelected(select);
    }
    /**
     * @return Returns the someHS.
     */
    public boolean isSomeHS()
    {
        return someHSBox.isSelected();
    }
    /**
     * @param someHS set the someHSBox
     */
    public void setSomeHS(boolean someHS)
    {
        someHSBox.setSelected(someHS);
    }

    /**
     * @param enable	enable the different fields of this
     * 					panel
     */
    public void setEditable(boolean enable)
    {
        someHSBox.setEnabled(enable);
        hsGradBox.setEnabled(enable);
        someCollegeBox.setEnabled(enable);
        collegeDegBox.setEnabled(enable);
        someGradBox.setEnabled(enable);
        mastersBox.setEnabled(enable);
        professionalBox.setEnabled(enable);
    }

    /* 
     * One of the boxes was selected 
     */
    public void actionPerformed(ActionEvent event)
    {
        cg.setDirty(true);
    }
}
