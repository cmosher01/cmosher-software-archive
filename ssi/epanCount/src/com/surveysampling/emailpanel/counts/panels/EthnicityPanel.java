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
public class EthnicityPanel extends JPanel implements ActionListener
{

    JCheckBox blackBox = new JCheckBox("Black");
    JCheckBox hispBox = new JCheckBox("Hispanic");
    JCheckBox whiteBox = new JCheckBox("White");
    JCheckBox asianBox = new JCheckBox("Asian");
    JCheckBox pacBox = new JCheckBox("Pacific");
    JCheckBox indBox = new JCheckBox("Indian");
    JCheckBox otherBox = new JCheckBox ("Other");
    CountsGUI cg;
    public EthnicityPanel(CountsGUI cg)
    {
        super();
        this.cg = cg;
        setLayout(new FlowLayout(FlowLayout.LEFT, 1,0));
        add(new JLabel(" eth: "));
        add(blackBox);
        blackBox.addActionListener(this);
        add(hispBox);
        hispBox.addActionListener(this);
        add(whiteBox);
        whiteBox.addActionListener(this);
        add(asianBox);
        asianBox.addActionListener(this);
        add(pacBox);
        pacBox.addActionListener(this);
        add(indBox);
        indBox.addActionListener(this);
        add(otherBox);
        otherBox.addActionListener(this);
    }
    
    /**
     * @return 	true if the asianBox is selected
     * 			otherwise, false
     */
    public boolean isAsian()
    {
        return asianBox.isSelected();
    }
    /**
     * @param select set the asianBox
     */
    public void setAsian(boolean select)
    {
        asianBox.setSelected(select);
    }
    /**
     * @return  true, if the blackBox is selected
     * 			otherwise, false
     */
    public boolean isBlack()
    {
        return blackBox.isSelected();
    }
    /**
     * @param select set the blackBox
     */
    public void setBlack(boolean select)
    {
        blackBox.setSelected(select);
    }
    /**
     * @return  true, if the hispBox is selected
     * 			otherwise, false
     */
    public boolean isHisp()
    {
        return hispBox.isSelected();
    }
    /**
     * @param select set the hispBox
     */
    public void setHisp(boolean select)
    {
        hispBox.setSelected(select);
    }
    /**
     * @return  true, if the indBox is selected
     * 			otherwise, false
     */
    public boolean isInd()
    {
        return indBox.isSelected();
    }
    /**
     * @param select set indBox
     */
    public void setInd(boolean select)
    {
        indBox.setSelected(select);
    }
    /**
     * @return  true, if the otherBox is selected
     * 			otherwise, false
     */
    public boolean isOther()
    {
        return otherBox.isSelected();
    }
    /**
     * @param select set the otherBox
     */
    public void setOther(boolean select)
    {
        otherBox.setSelected(select);
    }
    /**
     * @return  true, if the pacBox is selected
     * 			otherwise, false
     */
    public boolean isPac()
    {
        return pacBox.isSelected();
    }
    /**
     * @param select set the pacBox to select
     */
    public void setPac(boolean select)
    {
        pacBox.setSelected(select);
    }
    /**
     * @return  true, if the whiteBox is selected
     * 			otherwise, false
     */
    public boolean isWhite()
    {
        return whiteBox.isSelected();
    }
    /**
     * @param select set the whiteBox
     */
    public void setWhite(boolean select)
    {
        whiteBox.setSelected(select);
    }


    /**
     * @param enable enable/disable the fields
     */
    public void setFieldsEnabled(boolean enable)
    {
        blackBox.setEnabled(enable);
        hispBox.setEnabled(enable);
        whiteBox.setEnabled(enable);
        asianBox.setEnabled(enable);
        pacBox.setEnabled(enable);
        indBox.setEnabled(enable);
        otherBox.setEnabled(enable);
    }

    /* 
     * One of the boxes was selected
     */
    public void actionPerformed(ActionEvent arg0)
    {
        cg.setDirty(true);
    }
}
