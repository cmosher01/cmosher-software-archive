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
public class MarriedPanel extends JPanel implements ActionListener
{
    JCheckBox singleBox = new JCheckBox("Single");
    JCheckBox divorceBox = new JCheckBox("Widow/Div/Sep");
    JCheckBox marriedBox = new JCheckBox("Married");
    JCheckBox partnerBox = new JCheckBox("Partner");
    boolean single, widow, married, partner;
    private CountsGUI cg;
    
    public MarriedPanel(CountsGUI cg)
    {
        super();
        this.cg = cg;
        setLayout(new FlowLayout(FlowLayout.LEFT,1,0));
        add(new JLabel(" mar:"));
        add(singleBox);
        add(divorceBox);
        add(marriedBox);
        add(partnerBox);
        singleBox.addActionListener(this);
        divorceBox.addActionListener(this);
        marriedBox.addActionListener(this);
        partnerBox.addActionListener(this);
    }
    
    /**
     * @return Returns the married.
     */
    public boolean isMarried()
    {
        return marriedBox.isSelected();
    }
    /**
     * @param married The married to set.
     */
    public void setMarried(boolean married)
    {
        marriedBox.setSelected(married);
    }
    /**
     * @return Returns the partner.
     */
    public boolean isPartner()
    {
        return partnerBox.isSelected();
    }
    /**
     * @param partner The partner to set.
     */
    public void setPartner(boolean partner)
    {
        partnerBox.setSelected(partner);
    }
    /**
     * @return Returns the single.
     */
    public boolean isSingle()
    {
        return singleBox.isSelected();
    }
    
    /**
     * @param single The single to set.
     */
    public void setSingle(boolean single)
    {
        singleBox.setSelected(single);
    }
    /**
     * @return Returns the widow.
     */
    public boolean isDivorce()
    {
        return divorceBox.isSelected();
    }
    /**
     * @param widow The widow to set.
     */
    public void setDivorce(boolean widow)
    {
        divorceBox.setSelected(widow);
    }

    /**
     * @param enable
     */
    public void setEditable(boolean enable)
    {
        singleBox.setEnabled(enable);
        divorceBox.setEnabled(enable);
        marriedBox.setEnabled(enable);
        partnerBox.setEnabled(enable);
    }

    /**
     * Handles the actions of this panel.
     * When one of the check boxes have been checked
     */
    public void actionPerformed(ActionEvent event)
    {
        cg.setDirty(true);
    }
}
