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
public class IncomePanel extends JPanel implements ActionListener
{

    JCheckBox inc0 = new JCheckBox();
    JCheckBox inc20 = new JCheckBox();
    JCheckBox inc30 = new JCheckBox();
    JCheckBox inc40 = new JCheckBox();
    JCheckBox inc50 = new JCheckBox();
    JCheckBox inc60 = new JCheckBox();
    JCheckBox inc75 = new JCheckBox();
    JCheckBox inc100 = new JCheckBox();
    JCheckBox inc150 = new JCheckBox();
    JCheckBox incPNA = new JCheckBox("PNA");
    JLabel $Klabel = new JLabel("  ($K)");
    
    JLabel inc0label = new JLabel("0");
    JLabel inc20label = new JLabel("20");
    JLabel inc30label = new JLabel("30");
    JLabel inc40label = new JLabel("40");
    JLabel inc50label = new JLabel("50");
    JLabel inc60label = new JLabel("60");
    JLabel inc75label = new JLabel("75");
    JLabel inc100label = new JLabel("100");
    JLabel inc150label = new JLabel("150+");
    CountsGUI cg;
    
    public IncomePanel(CountsGUI cg)
    {
        super();
        this.cg = cg;
        setLayout(new FlowLayout(FlowLayout.LEFT,1,0));
        add(new JLabel(" inc:"));
        
		add(inc0label);
        add(inc0);
        inc0.addActionListener(this);
        add(inc20label);
        add(inc20);
        inc20.addActionListener(this);
        add(inc30label);
        add(inc30);
        inc30.addActionListener(this);
        add(inc40label);
        add(inc40);
        inc40.addActionListener(this);
        add(inc50label);
        add(inc50);
        inc50.addActionListener(this);
        add(inc60label);
        add(inc60);
        inc60.addActionListener(this);
        add(inc75label);
        add(inc75);
        inc75.addActionListener(this);
        add(inc100label);
        add(inc100);
        inc100.addActionListener(this);
        add(inc150label);
        add(inc150);
        inc150.addActionListener(this);
        add($Klabel);
        add(new JLabel("  "));
        add(incPNA);
        incPNA.addActionListener(this);
        
    }
    /**
     * @param binc0 The binc0 to set.
     */
    public void setInc0(boolean binc0)
    {
        inc0.setSelected(binc0);
    }
    /**
     * 
     * @return
     */
    public boolean isInc0()
    {
        return inc0.isSelected();
    }
    /**
     * @return Returns the binc20.
     */
    public boolean isInc20()
    {
        return inc20.isSelected();
    }
    /**
     * @param binc20 The binc20 to set.
     */
    public void setInc20(boolean binc20)
    {
        inc20.setSelected(binc20);
    }
    /**
     * @return Returns the binc30.
     */
    public boolean isInc30()
    {
        return inc30.isSelected();
    }
    /**
     * @param binc30 The binc30 to set.
     */
    public void setInc30(boolean binc30)
    {
        inc30.setSelected(binc30);
    }
    /**
     * @return Returns the binc40.
     */
    public boolean isInc40()
    {
        return inc40.isSelected();
    }
    /**
     * @param binc40 The binc40 to set.
     */
    public void setInc40(boolean binc40)
    {
        inc40.setSelected(binc40);
    }
    /**
     * @return Returns the binc50.
     */
    public boolean isInc50()
    {
        return inc50.isSelected();
    }
    /**
     * @param binc50 The binc50 to set.
     */
    public void setInc50(boolean binc50)
    {
        inc50.setSelected(binc50);
    }
    /**
     * @return Returns the binc60.
     */
    public boolean isInc60()
    {
        return inc60.isSelected();
    }
    /**
     * @param binc60 The binc60 to set.
     */
    public void setInc60(boolean binc60)
    {
        inc60.setSelected(binc60);
    }
    /**
     * @return Returns the binc75.
     */
    public boolean isInc75()
    {
        return inc75.isSelected();
    }
    /**
     * @param binc75 The binc75 to set.
     */
    public void setInc75(boolean binc75)
    {
        inc75.setSelected(binc75);
    }
    /**
     * @return Returns the binc100.
     */
    public boolean isInc100()
    {
        return inc100.isSelected();
    }
    /**
     * @param binc100 The binc100 to set.
     */
    public void setInc100(boolean binc100)
    {
        inc100.setSelected(binc100);
        
    }
    /**
     * @return Returns the binc150.
     */
    public boolean isInc150()
    {
        return inc150.isSelected();
    }
    /**
     * @param binc150 The binc150 to set.
     */
    public void setInc150(boolean binc150)
    {
        inc150.setSelected(binc150);
    }
    /**
     * @return Returns the bincPNA.
     */
    public boolean isIncPNA()
    {
        return incPNA.isSelected();
    }
    /**
     * @param bincPNA The bincPNA to set.
     */
    public void setIncPNA(boolean bincPNA)
    {
        incPNA.setSelected(bincPNA);
    }
    /**
     * @param enable
     */
    public void setFieldsEnable(boolean enable)
    {
        inc0.setEnabled(enable);
        inc20.setEnabled(enable);
        inc30.setEnabled(enable);
        inc40.setEnabled(enable);
        inc50.setEnabled(enable);
        inc60.setEnabled(enable);
        inc75.setEnabled(enable);
        inc100.setEnabled(enable);
        inc150.setEnabled(enable);
        incPNA.setEnabled(enable);
        $Klabel.setEnabled(enable);
        inc0label.setEnabled(enable);
        inc20label.setEnabled(enable);
        inc30label.setEnabled(enable);
        inc40label.setEnabled(enable);
        inc50label.setEnabled(enable);
        inc60label.setEnabled(enable);
        inc75label.setEnabled(enable);
        inc100label.setEnabled(enable);
        inc150label.setEnabled(enable);
    }
    /** 
     * One of the check boxes was selected
     */
    public void actionPerformed(ActionEvent event)
    {
        cg.setDirty(true);
    }
}
